package com.eunycesoft.psms.security;

import com.eunycesoft.psms.data.entity.User;
import com.eunycesoft.psms.data.repository.PersonnelRepository;
import com.eunycesoft.psms.data.repository.UserRepository;
import com.eunycesoft.psms.data.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PersonnelRepository personnelRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PersonnelRepository personnelRepository) {
        this.userRepository = userRepository;
        this.personnelRepository = personnelRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    getAuthorities(user));
        }
    }

    private List<GrantedAuthority> getAuthorities(User user) {
        var roles = new ArrayList<Role>();
        roles.add(user.getMainRole());
        if (!user.getMainRole().equals(Role.STUDENT)) {
            var pers = personnelRepository.findById(user.getId());
            if (pers.isPresent()) roles.addAll(pers.get().getOtherRoles());
        }
        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }
}
