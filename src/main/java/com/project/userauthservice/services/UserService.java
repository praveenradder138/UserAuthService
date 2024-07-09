package com.project.userauthservice.services;

import com.project.userauthservice.exceptions.*;
import com.project.userauthservice.models.OAuthAccount;
import com.project.userauthservice.models.Role;
import com.project.userauthservice.models.User;
import com.project.userauthservice.repositories.OAuthAccountRepository;
import com.project.userauthservice.repositories.RoleRepostitory;
import com.project.userauthservice.repositories.UserRepository;
import com.project.userauthservice.security.models.CustomerUserDetails;
import com.project.userauthservice.utils.Constants;
import com.project.userauthservice.utils.JwtUtils;
import jakarta.mail.MessagingException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepostitory roleRepostitory;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final JwtUtils jwtUtils;

    UserService(UserRepository userRepository,RoleRepostitory roleRepostitory,
                OAuthAccountRepository oAuthAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                EmailService emailService, JwtUtils jwtUtils){
        this.userRepository=userRepository;
        this.roleRepostitory=roleRepostitory;
        this.oAuthAccountRepository=oAuthAccountRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.emailService=emailService;
        this.jwtUtils=jwtUtils;
    }
    public User signup(String name, String email, String password) throws ExistingUserException {

        Optional<User> optionalUser=userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            throw new ExistingUserException("Email "+email+" already exists");
        }
        Role role= roleRepostitory.findByValue(Constants.ROLE_CUSTOMER)
                .orElseGet(()-> roleRepostitory.save(new Role(Constants.ROLE_CUSTOMER)));

        String token=UUID.randomUUID().toString();
        User user=User.builder().name(name)
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .isEmailVerified(false)
                .emailVerificationToken(token)
                .emailTokenExpirationTime(new Date(System.currentTimeMillis() + 3600 * 1000))
                .roles(List.of(role))
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public String saveUserAndGetOAuthToken(String name, String email, String provider, String providerId) {
        Optional<User> optionalUser=userRepository.findByEmail(email);
        User user;
        if(optionalUser.isEmpty()) {
            Role role = roleRepostitory.findByValue(Constants.ROLE_CUSTOMER)
                    .orElseGet(() -> roleRepostitory.save(new Role(Constants.ROLE_CUSTOMER)));
            user = User.builder().name(name)
                    .email(email)
                    .password(null)
                    .isEmailVerified(true)
                    .emailVerificationToken(null)
                    .emailTokenExpirationTime(null)
                    .roles(List.of(role))
                    .build();
            userRepository.save(user);
        }else {
            user = optionalUser.get();
        }
        Optional<OAuthAccount> existingOAuthAccount = oAuthAccountRepository.findByProviderAndProviderId(provider, providerId);
        if (existingOAuthAccount.isEmpty()) {
            OAuthAccount oAuthAccount = OAuthAccount.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .user(user)
                    .build();
            oAuthAccountRepository.save(oAuthAccount);
        }

        return jwtUtils.generateToken(new CustomerUserDetails(optionalUser.get()));

    }

    public void sendVerificationEmail(String email) throws MessagingException {
        Optional<User> optionalUser=userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            emailService.sendVerificationEmail(email,user.getEmailVerificationToken());
        }
    }


    public User verifyEmail(String token) throws EmailVerificationTokenException {
        Optional<User> optionalUser=userRepository.findByEmailVerificationToken(token);
        if(optionalUser.isEmpty()){
            throw new EmailVerificationTokenException("Invalid Token");
        }
        User user = optionalUser.get();
        if (user.getEmailTokenExpirationTime().before(new Date())) {
            throw new EmailVerificationTokenException("The token has expired. Request for a new token");
        }
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailTokenExpirationTime(null);
        userRepository.save(user);
        return user;

        }

    public String signin(String email, String password) throws NotExistingUserException, IncorrectPasswordException, UnverifiedEmailException {
        Optional<User> optionalUser=userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new NotExistingUserException("User with email " + email + " doesn't exist.");
        }
        User user=optionalUser.get();
        if(Boolean.FALSE.equals(user.isEmailVerified())){
            throw new UnverifiedEmailException("User with email "+email+ " doesn't exist or the email is not verified. Please verify the email");
        }
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            throw new IncorrectPasswordException("Password entered is incorrect");
        }
        return jwtUtils.generateToken(new CustomerUserDetails(user));
    }

}
