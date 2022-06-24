package victor.training.clean.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import victor.training.clean.domain.entity.User;
import victor.training.clean.infra.LdapApi;
import victor.training.clean.infra.LdapUserDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class Adapter {
    @Autowired
    private LdapApi ldapApi;

    public User retrieveUser(String username) {
        List<User> list = ldapApi
                .searchUsingGET(null, null, username.toUpperCase())
                .stream().map(this::convert)
                .collect(Collectors.toList());
        if (list.size() != 1) {
            throw new IllegalArgumentException("There is no single user matching username " + username);
        }
        return list.get(0);
    }

    private User convert(LdapUserDto ldapUser) {
        String fullName = ldapUser.getFname() + " " + ldapUser.getLname().toUpperCase();
        return new User(ldapUser.getUid(), fullName, ldapUser.getWorkEmail());
    }


}