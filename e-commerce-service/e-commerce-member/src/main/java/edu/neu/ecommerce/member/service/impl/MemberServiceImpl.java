package edu.neu.ecommerce.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.neu.ecommerce.member.dao.MemberDao;
import edu.neu.ecommerce.member.dao.MemberLevelDao;
import edu.neu.ecommerce.member.entity.MemberEntity;
import edu.neu.ecommerce.member.entity.MemberLevelEntity;
import edu.neu.ecommerce.member.exception.PhoneExistException;
import edu.neu.ecommerce.member.exception.UsernameExistException;
import edu.neu.ecommerce.member.service.MemberService;
import edu.neu.ecommerce.member.vo.MemberLoginVo;
import edu.neu.ecommerce.member.vo.MemberRegistVo;
import edu.neu.ecommerce.member.vo.SocialUser;
import edu.neu.ecommerce.utils.HttpUtils;
import edu.neu.ecommerce.utils.PageUtils;
import edu.neu.ecommerce.utils.Query;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public MemberEntity login(SocialUser vo) {
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("weibo_uid", vo.getUid()));
        if(memberEntity != null){
            //用户已经注册
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(vo.getAccess_token());
            update.setExpiresIn(vo.getExpires_in());

            this.baseMapper.updateById(update);
            memberEntity.setAccessToken(vo.getAccess_token());
            memberEntity.setExpiresIn(vo.getExpires_in());
            return memberEntity;
        } else {
            // 3.未注册
            MemberEntity member = new MemberEntity();
            try {
                // 查询当前社交用户的社交账号信息，封装会员信息（查询结果不影响注册结果，所以使用try/catch）
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("access_token", vo.getAccess_token());
                queryMap.put("uid", vo.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "post", new HashMap<String, String>(), queryMap);
                if (response.getStatusLine().getStatusCode() == 200) {
                    //查询成功
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String gender = jsonObject.getString("gender");
                    String profileImageUrl = jsonObject.getString("profile_image_url");
                    // 封装注册信息
                    member.setNickname(name);
                    member.setGender("m".equals(gender) ? 1 : 0);
                    member.setHeader(profileImageUrl);
                    member.setCreateTime(new Date());
                }
            } catch (Exception e) {

            }
            member.setWeiboUid(vo.getUid());
            member.setAccessToken(vo.getAccess_token());
            member.setExpiresIn(vo.getExpires_in());
            //把用户信息插入到数据库中
            baseMapper.insert(member);
            return member;
        }
    }

    @Override
    public void regist(MemberRegistVo vo){
        MemberEntity entity = new MemberEntity();

        //设置默认等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());

        //为了让controller感知到异常
        checkPhoneUnique(vo.getPhone());
        checkUsernameUnique(vo.getUserName());

        //设置
        entity.setMobile(vo.getPhone());
        //检查用户名和手机号是否唯一
        entity.setUsername(vo.getUserName());
        entity.setNickname(vo.getUserName());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassWord());
        //设置密码：密码加密存储
        entity.setPassword(encode);

        baseMapper.insert(entity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer mobile = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(mobile > 0){
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if(count > 0){
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        //1，去数据库查询
        MemberEntity entity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct)
                .or()
                .eq("mobile", loginacct));
        if(entity == null){
            //登录失败
            return null;
        }else{
            //1，获取到数据库的字段
            String password1 = entity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            /* 密码匹配 */
            if (passwordEncoder.matches(password, password1)) {
                return entity;
            }else{
                return null;
            }
        }
    }
}