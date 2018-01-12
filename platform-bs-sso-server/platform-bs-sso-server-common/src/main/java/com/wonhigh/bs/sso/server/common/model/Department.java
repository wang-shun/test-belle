package com.wonhigh.bs.sso.server.common.model;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

/**
 * sso��¼�û� 
 * 
 */
@Entry(objectClasses = {"organizationalUnit", "top"}, base = "ou=Departments")
public class Department {
	
	@Id
    private Name dn;
	
	//�������
	@Attribute(name = "ou")
	@DnAttribute(value="ou", index=1)
    @Transient
    private String departmentName;
	
	//����
	@Attribute(name = "description")
	private String description;

}