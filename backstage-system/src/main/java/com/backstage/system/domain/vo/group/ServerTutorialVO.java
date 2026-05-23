package com.backstage.system.domain.vo.group;

import java.util.List;

/**
 * 服务器教程VO
 * 
 * @author system
 * @date 2026-05-14
 */
public class ServerTutorialVO {
    
    /** 教程内容（HTML或Markdown格式） */
    private String tutorial;
    
    /** 教程视频链接 */
    private String videoUrl;
    
    /** 教程步骤列表 */
    private List<TutorialStep> steps;
    
    /** 服务器配置信息 */
    private ServerConfig serverConfig;
    
    /** 常见问题列表 */
    private List<FaqItem> faq;
    
    public ServerTutorialVO() {
    }
    
    // Getter and Setter
    
    public String getTutorial() {
        return tutorial;
    }
    
    public void setTutorial(String tutorial) {
        this.tutorial = tutorial;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public List<TutorialStep> getSteps() {
        return steps;
    }
    
    public void setSteps(List<TutorialStep> steps) {
        this.steps = steps;
    }
    
    public ServerConfig getServerConfig() {
        return serverConfig;
    }
    
    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
    
    public List<FaqItem> getFaq() {
        return faq;
    }
    
    public void setFaq(List<FaqItem> faq) {
        this.faq = faq;
    }
    
    /**
     * 教程步骤
     */
    public static class TutorialStep {
        /** 步骤序号 */
        private Integer step;
        
        /** 步骤标题 */
        private String title;
        
        /** 步骤内容 */
        private String content;
        
        /** 步骤配图URL（可选） */
        private String imageUrl;
        
        public TutorialStep() {
        }
        
        public TutorialStep(Integer step, String title, String content) {
            this.step = step;
            this.title = title;
            this.content = content;
        }
        
        public Integer getStep() {
            return step;
        }
        
        public void setStep(Integer step) {
            this.step = step;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
    
    /**
     * 服务器配置信息
     */
    public static class ServerConfig {
        /** 服务器IP */
        private String serverIp;
        
        /** SSH端口 */
        private Integer sshPort;
        
        /** 默认用户名 */
        private String defaultUsername;
        
        /** 密码重置说明 */
        private String passwordResetGuide;
        
        public ServerConfig() {
        }
        
        public String getServerIp() {
            return serverIp;
        }
        
        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }
        
        public Integer getSshPort() {
            return sshPort;
        }
        
        public void setSshPort(Integer sshPort) {
            this.sshPort = sshPort;
        }
        
        public String getDefaultUsername() {
            return defaultUsername;
        }
        
        public void setDefaultUsername(String defaultUsername) {
            this.defaultUsername = defaultUsername;
        }
        
        public String getPasswordResetGuide() {
            return passwordResetGuide;
        }
        
        public void setPasswordResetGuide(String passwordResetGuide) {
            this.passwordResetGuide = passwordResetGuide;
        }
    }
    
    /**
     * 常见问题项
     */
    public static class FaqItem {
        /** 问题 */
        private String question;
        
        /** 答案 */
        private String answer;
        
        public FaqItem() {
        }
        
        public FaqItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
        
        public String getQuestion() {
            return question;
        }
        
        public void setQuestion(String question) {
            this.question = question;
        }
        
        public String getAnswer() {
            return answer;
        }
        
        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}