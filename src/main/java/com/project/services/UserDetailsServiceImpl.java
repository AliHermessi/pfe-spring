package com.project.services;

import com.project.models.Role;
import com.project.models.User;
import com.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String cin) throws UsernameNotFoundException {
        User user = userRepository.findByCin(cin);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with cin: " + cin);
        }
        List<Role> userRoles = user.getRole();
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : userRoles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getCin(), user.getPassword(), authorities);
        System.out.println("Loaded user details: " + userDetails);
        System.out.println("Loaded user authorities: " + authorities);

        return userDetails;
    }

}

