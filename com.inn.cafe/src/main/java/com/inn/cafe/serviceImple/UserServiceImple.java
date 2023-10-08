package com.inn.cafe.serviceImple;


import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUserDetailsServices;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.POJO.User;
import com.inn.cafe.constants.CafeConstant;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImple implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil ;
    @Autowired
    CustomerUserDetailsServices customerUserDetailsServices;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private boolean validateSignUpMap(Map<String,String> requestMap){
        if(requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password")){
            return true;
        }else{
            return false;
        }
    }


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside singUp of UserServiceImpl",requestMap);
        if (validateSignUpMap(requestMap)){
            User user =userDao.findByEmailId(requestMap.get("email"));
            if(Objects.isNull(user)){
                userDao.save(getUserFromMap(requestMap));
                return CafeUtils.getResponseEntity(CafeConstant.REGISTRATION_SUCCESSFULL,HttpStatus.OK);
            }else{
               return  CafeUtils.getResponseEntity(CafeConstant.USER_ALREADY_EXIST,HttpStatus.BAD_REQUEST);
            }
        }else {
            return CafeUtils.getResponseEntity(CafeConstant.BAD_REQUEST, HttpStatus.BAD_REQUEST);
        }
    }



    private User getUserFromMap(Map<String,String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setRole("user");
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setStatus("false");
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));



        return user;
    }


    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside Login of serviceimple",requestMap);
        String properErrorMessage = "BAD REQUEST pola ";
        try{
            Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );

            System.out.println("After Authenticate");
            System.out.println(requestMap);

            if(authentication.isAuthenticated()){
                System.out.println("authentication.isAuthenticated()");
                System.out.println(requestMap);
                if(customerUserDetailsServices.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    System.out.println("customerUserDetailsServices.getUserDetail().getStatus().equalsIgnoreCase");
                    System.out.println(requestMap);
                    return new ResponseEntity<String>("{\"token\":\""+
                     jwtUtil.generateToken(customerUserDetailsServices.getUserDetail().getEmail(),customerUserDetailsServices.getUserDetail().getRole())+ "\"}"
                    ,HttpStatus.OK);
                }else {
                    return new ResponseEntity<String>("{\"message\":\""+"User Not Verifyied."+"\"}",HttpStatus.BAD_REQUEST);
                }
            }
        }catch(Exception ex){
            properErrorMessage= ex.getMessage();
            ex.printStackTrace();
        }
        return new ResponseEntity<String>(properErrorMessage+" lola",HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try
        {
            if(jwtFilter.isAdmin()){
               Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
               if(!optional.isEmpty()){
                   userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                   sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                   return CafeUtils.getResponseEntity("User Status Updated Successfully",HttpStatus.OK);
               }else{
                   CafeUtils.getResponseEntity("User id doesn't exist",HttpStatus.OK);
               }
            }else{
                return CafeUtils.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {

        return CafeUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User user = userDao.findByEmailId(jwtFilter.getUserName());
            if(!user.equals(null)){
                if(passwordEncoder.matches(requestMap.get("oldPassword"), user.getPassword())){
                    user.setPassword(passwordEncoder.encode(requestMap.get("newPassword")));
                    userDao.save(user);
                    return CafeUtils.getResponseEntity("Password Updated Successfully!",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Incorrect Old Password",HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmailId(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())){
                emailUtils.forgotPasswordSendEmail(user.getEmail(),"Credentials By CafeManagementSystem","lola");
            }
            return CafeUtils.getResponseEntity("Check Your Mail For Creadentials",HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String email, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getUserName());
        if(status!=null && status.equalsIgnoreCase("true")){
           emailUtils.sendSimpleMessage(jwtFilter.getUserName(),"Account Approved","User:- "+email+"\n is approved by \nADMIN:-"+jwtFilter.getUserName(),allAdmin);
        }else{
            emailUtils.sendSimpleMessage(jwtFilter.getUserName(),"Account Disabled","User:- "+email+"\n is disabled by \nADMIN:-"+jwtFilter.getUserName(),allAdmin);
        }
    }
}
