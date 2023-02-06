package io.jenkins.plugins.sample;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ArgumentListBuilder;
import io.jenkins.plugins.util.JsonHelper;
import io.jenkins.plugins.util.OkHttpUtils;
import io.jenkins.plugins.vo.AccessTokenVO;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private static final String CLIENT_TOKEN = "/oauth/oauth/token?grant_type=client_credentials&client_id=%s&client_secret=%s";

    //    private final String name;
    private final String repoType;
    private final String repoCode;

    @DataBoundConstructor
    public HelloWorldBuilder(String repoType, String repoCode) {
        this.repoType = repoType;
        this.repoCode = repoCode;
    }

    @NonNull
    public String getRepoType() {
        return repoType;
    }

    @NonNull
    public String getRepoCode() {
        return repoCode;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        SampleConfiguration sampleConfiguration = SampleConfiguration.get();

//        String clientTokenUrl = String.format(CLIENT_TOKEN, sampleConfiguration.getClientId(), sampleConfiguration.getClientSecret());

        String resp = OkHttpUtils.builder()
                .url(sampleConfiguration.getApiUrl() + "/oauth/oauth/token")
                .addParam("grant_type", "client_credentials")
                .addParam("client_id", sampleConfiguration.getClientId())
                .addParam("client_secret", sampleConfiguration.getClientSecret())
                .post(false)
                .sync();
        AccessTokenVO accessTokenVO = JsonHelper.unmarshalByJackson(resp, AccessTokenVO.class);
        String accessToken = accessTokenVO.getAccessToken();
        listener.getLogger().println("accessToken is " + accessToken);

//        OkHttpClient client = new OkHttpClient();
//        //创建一个Request
//
//        Request request = new Request.Builder()
//                .url(clientTokenUrl)
//                .post(null)
//                .build();
//        //通过client发起请求
//        okhttp3.Response execute = client.newCall(request).execute();
//        if (execute.isSuccessful()) {
//            String resp = execute.body().string();
//
//        }

        ArgumentListBuilder loginCmd = new ArgumentListBuilder();
        loginCmd.add("docker")
                .add("login")
                .add(env.get("REGISTRY"))
                .add("-u")
                .add(env.get("REGISTRY_USERNAME"))
                .add("-p")
                .add(env.get("REGISTRY_PASSWORD"));
        if (launcher.launch().cmds(loginCmd).stdout(listener).start().join() != 0) {
            run.setResult(Result.FAILURE);
            throw new RuntimeException("docker login failed!");
        }

//        ArgumentListBuilder buildCmd = new ArgumentListBuilder();
//        buildCmd.add("docker")
//                .add("build")
//                .add("-t")
//                .add("${env.REGISTRY}/${env.REPOSITORY}/${env.PROJECT_NAME}:1.0.0")
//                .add("--file")
//                .add("Dockerfile")
//                .add(".");
//        if (launcher.launch().cmds(buildCmd).stdout(listener).start().join() != 0) {
//            run.setResult(Result.FAILURE);
//            throw new RuntimeException("docker build failed!");
//        }
//
//        ArgumentListBuilder pushCmd = new ArgumentListBuilder();
//        pushCmd.add("docker")
//                .add("push")
//                .add("${env.REGISTRY}/${env.REPOSITORY}/${env.PROJECT_NAME}:1.0.0");
//        if (launcher.launch().cmds(pushCmd).stdout(listener).start().join() != 0) {
//            run.setResult(Result.FAILURE);
//            throw new RuntimeException("docker build failed!");
//        }
        run.setResult(Result.SUCCESS);
    }

    @Symbol("C7nDockerBuild")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.HelloWorldBuilder_DescriptorImpl_DisplayName();
        }

    }

}
