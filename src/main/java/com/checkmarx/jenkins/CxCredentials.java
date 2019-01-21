package com.checkmarx.jenkins;

import com.checkmarx.jenkins.exception.CxCredentialsException;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cx.restclient.common.ErrorMessage;

import hudson.model.Item;
import hudson.model.Run;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;


//resolve between global or specific and username+pswd or credential manager
public class CxCredentials {

    private String serverUrl;
    private String username;
    private String pswd;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }


    public static CxCredentials resolveCred(CxScanBuilder cxScanBuilder, CxScanBuilder.DescriptorImpl descriptor, Run<?, ?> run) {
        CxCredentials ret = new CxCredentials();
        if (cxScanBuilder.isUseOwnServerCredentials()) {
            ret.setServerUrl(cxScanBuilder.getServerUrl());
            if (StringUtils.isNotEmpty(cxScanBuilder.getCredentialsId())) {
                StandardUsernamePasswordCredentials c = CredentialsProvider.findCredentialById(cxScanBuilder.getCredentialsId(), StandardUsernamePasswordCredentials.class, run, Collections.<DomainRequirement>emptyList());
                ret.setUsername(c != null ? c.getUsername() : "");
                ret.setPswd(c != null ? c.getPassword().getPlainText() : "");
                return ret;

            } else {
                ret.setUsername(StringUtils.defaultString(cxScanBuilder.getUsername()));
                ret.setPswd(StringUtils.defaultString(cxScanBuilder.getPasswordPlainText()));
                return ret;
            }

        } else {
            ret.setServerUrl(descriptor.getServerUrl());
            if (StringUtils.isNotEmpty(descriptor.getCredentialsId())) {
                StandardUsernamePasswordCredentials c = CredentialsProvider.findCredentialById(descriptor.getCredentialsId(), StandardUsernamePasswordCredentials.class, run, Collections.<DomainRequirement>emptyList());
                ret.setUsername(c != null ? c.getUsername() : "");
                ret.setPswd(c != null ? c.getPassword().getPlainText() : "");
                return ret;

            } else {
                ret.setUsername(StringUtils.defaultString(descriptor.getUsername()));
                ret.setPswd(StringUtils.defaultString(descriptor.getPasswordPlainText()));
                return ret;
            }
        }
    }


    public static CxCredentials resolveCred(boolean useOwnServerCredentials, String serverUrl, String username, String pwd, String credentialsId, CxScanBuilder.DescriptorImpl descriptor, Item item) throws CxCredentialsException {

        CxCredentials ret = new CxCredentials();
        if (useOwnServerCredentials) {
            ret.setServerUrl(serverUrl);
            if (StringUtils.isNotEmpty(credentialsId)) {

                StandardUsernamePasswordCredentials c = CredentialsMatchers.firstOrNull(CredentialsProvider.lookupCredentials(
                                StandardUsernamePasswordCredentials.class,
                                item,
                                null,
                                Collections.<DomainRequirement>emptyList()),
                        CredentialsMatchers.withId(credentialsId));

                ret.setUsername(c != null ? c.getUsername() : "");
                ret.setPswd(c != null ? c.getPassword().getPlainText() : "");
                return ret;

            } else {
                ret.setUsername(StringUtils.defaultString(username));
                ret.setPswd(StringUtils.defaultString(pwd));
                return ret;
            }

        } else {
            ret.setServerUrl(descriptor.getServerUrl());
            if (StringUtils.isNotEmpty(descriptor.getCredentialsId())) {

                StandardUsernamePasswordCredentials c = CredentialsMatchers.firstOrNull(CredentialsProvider.lookupCredentials(
                        StandardUsernamePasswordCredentials.class,
                        item,
                        null,
                        Collections.<DomainRequirement>emptyList()),
                        CredentialsMatchers.withId(descriptor.getCredentialsId()));

                ret.setUsername(c != null ? c.getUsername() : "");
                ret.setPswd(c != null ? c.getPassword().getPlainText() : "");
                return ret;

            } else {
                ret.setUsername(StringUtils.defaultString(descriptor.getUsername()));
                ret.setPswd(StringUtils.defaultString(descriptor.getPasswordPlainText()));
                return ret;
            }
        }
    }

    public static void validateCxCredentials(CxCredentials credentials) throws CxCredentialsException {
        if(StringUtils.isEmpty(credentials.getServerUrl()) ||
                StringUtils.isEmpty(credentials.getUsername()) ||
                StringUtils.isEmpty((credentials.getPswd()))){
            throw new CxCredentialsException(ErrorMessage.CHECKMARX_SERVER_CONNECTION_FAILED.getErrorMessage());
        }
    }
}