package io.jenkins.plugins.sample;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

/**
 * Example of Jenkins global configuration.
 */
@Extension
public class SampleConfiguration extends GlobalConfiguration {

    private String apiUrl;
    private String clientId;
    private String clientSecret;

    public SampleConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    /**
     * @return the singleton instance
     */
    public static SampleConfiguration get() {
        return ExtensionList.lookupSingleton(SampleConfiguration.class);
    }

    public String getApiUrl() {
        return apiUrl;
    }

    @DataBoundSetter
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        save();
    }

    public String getClientId() {
        return clientId;
    }

    @DataBoundSetter
    public void setClientId(String clientId) {
        this.clientId = clientId;
        save();
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @DataBoundSetter
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        save();
    }
//
//    /** @return the currently configured label, if any */
//    public String getLabel() {
//        return label;
//    }
//
//    /**
//     * Together with {@link #getLabel}, binds to entry in {@code config.jelly}.
//     * @param label the new value of this field
//     */
//    @DataBoundSetter
//    public void setLabel(String label) {
//        this.label = label;
//        save();
//    }

    public FormValidation doCheckLabel(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify a label.");
        }
        return FormValidation.ok();
    }

}
