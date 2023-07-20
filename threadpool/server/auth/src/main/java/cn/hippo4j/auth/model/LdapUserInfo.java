package cn.hippo4j.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Data
@Entry(objectClasses = {"inetOrgPerson", "top"})
public class LdapUserInfo {

    @JsonIgnore
    @Id
    private Name dn;

    @Attribute(name = "cn")
    @DnAttribute(value = "cn")
    private String userName;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "description")
    private String description;

    @Attribute(name = "telephoneNumber")
    private String telephoneNumber;

    @Attribute(name = "userPassword")
    private String password;

    @Attribute(name = "ou")
    private String organizational;

}
