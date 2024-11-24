//package com.mgbell.global.fcm;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//
//@Configuration
//@EnableScheduling
//class SchedulerConfig {
//    public ThreadPoolTaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(3);
//        scheduler.setThreadNamePrefix("my-scheduler-task");
//        scheduler.initialize();
//        return scheduler;
//    }
//}
