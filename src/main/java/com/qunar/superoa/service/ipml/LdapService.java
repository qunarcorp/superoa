package com.qunar.superoa.service.ipml;

import com.qunar.superoa.config.LdapProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * 用户来接LDAP管理用户信息
 */
@Service
class LdapService {

    @Autowired
    private LdapProperties ldapProperties;

    private static final String LDAP_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String LDAP_URL = "ldaps://qunarldap.cn2.qunar.com";
    private static final String LDAP_DOMAIN = "dc=***,dc=***";
    private static final String PUBLIC_ACCOUNT = "username";
    private static final String PUBLIC_PASSWORD = "********";

    public static void main(String[] args) throws NamingException {
        LdapService userAD = new LdapService();

        NamingEnumeration<SearchResult> en = userAD.searchBySortName("kt94",
                "name", "***", "***", "***", "***", "***", "***", "***");
        // NamingEnumeration<SearchResult> en = userAD.searchBySortName("kt94");

        if (en == null) {
            System.out.println("Have no NamingEnumeration.");
        }

        if (!en.hasMoreElements()) {
            System.out.println("Have no element.");
        } else {
            // 输出查到的数据
            while (en.hasMoreElements()) {
                SearchResult result = en.next();
                NamingEnumeration<? extends Attribute> attrs = result.getAttributes().getAll();
                while (attrs.hasMore()) {
                    Attribute attr = attrs.next();

                    if ("manager".equals(attr.getID())) {
                        String[] manArr = attr.get().toString().split(",");
                        if (manArr.length > 0) {
                            String[] manAttrArr = manArr[0].split("=");
                            if (manAttrArr.length > 1) {
                                System.out.println(attr.getID() + "=" + manAttrArr[1]);
                            }
                        }
                    } else {
                        System.out.println(attr.getID() + "=" + attr.get());
                    }
                }

                System.out.println("======================");

            }
        }

        boolean authenticate = userAD.authenticate("kttt", "111");
        System.out.println("authenticate: " + authenticate);
    }

    /**
     * 登陆认证
     * @param sortName
     * @param password
     * @return
     */
    public boolean authenticate(String sortName, String password) {
        if (sortName == null || "".equals(sortName)) {
            return false;
        }

        String account = sortName + "@qunarservers.com";

        LdapContext ladpContent = connectLdap(ldapProperties.getLdap_user(), ldapProperties.getLdap_pwd());
        try {
            ladpContent.addToEnvironment(Context.SECURITY_PRINCIPAL, account);
            ladpContent.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ladpContent.reconnect(null);
        } catch (NamingException e) {
            return  false;
        }

        return true;
    }

    /**
     * 根据短名搜索用户
     * @param sortName
     * @param attributes
     * @return
     */
    public NamingEnumeration<SearchResult> searchBySortName(String sortName, String... attributes) {
        String filter = "(&(objectclass=user)(objectcategory=user)(sAMAccountName=" + sortName + "))";

        return search(filter, attributes);
    }

    /**
     * 根据英文名称搜索用户
     * @param name
     * @param attributes
     * @return
     */
    public NamingEnumeration<SearchResult> searchByName(String name, String... attributes) {
        String filter = "(&(objectclass=user)(objectcategory=user)(name=" + name + "))";

        return search(filter, attributes);
    }

    /**
     * 根据搜索条件搜索
     * @param filter - Filter, @see <a href="http://go.microsoft.com/fwlink/?LinkID=143553">LDAP syntax help</a>
     * @param attributes - Returning attributes
     * @return
     */
    public NamingEnumeration<SearchResult> search(String filter, String... attributes) {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        if (attributes != null && attributes.length > 0) {
            searchControls.setReturningAttributes(attributes);
        }

        LdapContext ladpContent = connectLdap(ldapProperties.getLdap_user(), ldapProperties.getLdap_pwd());

        NamingEnumeration<SearchResult> en = null;
        try {
            // 三个参数分别为：1.上下文；2.要搜索的属性，如果为空或 null，则返回目标上下文中的所有对象；3.控制搜索的搜索控件，如果为 null，则使用默认的搜索控件
            en = ladpContent.search(ldapProperties.getLdap_basedn(), filter, searchControls);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return en;
    }

    /**
     * 连接LDAP
     * @param account - [Short name]@buyabs.corp
     * @param password - Your windows password
     * @return
     */
    public LdapContext connectLdap(String account, String password) {
        Hashtable<String, String> env = getLdapEnvironmentConfig(account, password);

        LdapContext context = null;

        try {
            context = new InitialLdapContext(env, null);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            // 连接失败日志打印
            e.printStackTrace();
        }

        return context;
    }

    /**
     * 获取LDAP环境配置
     * @param account
     * @param password
     * @return
     */
    private Hashtable<String, String> getLdapEnvironmentConfig(String account, String password) {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_FACTORY);
        // LDAP server
        env.put(Context.PROVIDER_URL, ldapProperties.getLdap_host());
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, account);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put("java.naming.referral", "follow");

        return env;
    }
}
