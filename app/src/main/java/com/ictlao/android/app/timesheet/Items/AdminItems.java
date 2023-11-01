package com.ictlao.android.app.timesheet.Items;

public class AdminItems {
    private String name = "";
    private String email = "";
    private String password = "";
    private String companyId = "";
    private String profile_url = "";
    private boolean enable = false;
    private String companySecretCode = "";
    public AdminItems(){}
    public AdminItems(String name,String email,String password,String companyId,
                      String profile_url, boolean enable,String companySecretCode){
        this.name = name;
        this.email = email;
        this.password = password;
        this.companyId = companyId;
        this.profile_url = profile_url;
        this.enable = enable;
        this.companySecretCode = companySecretCode;
    }

    public String getCompanySecretCode() {
        return companySecretCode;
    }

    public void setCompanySecretCode(String companySecretCode) {
        this.companySecretCode = companySecretCode;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}

