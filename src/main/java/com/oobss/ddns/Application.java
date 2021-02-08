package com.oobss.ddns;

import com.oobss.ddns.util.PropertiesFileUtils;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Hello world!
 * @author zhaomj
 */
public class Application {
    public static void main(String[] args) {
        Properties properties = PropertiesFileUtils.getPropertiesFromUserDir("application.properties");
        System.getProperties().putAll(properties);
        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            JobDetail job = newJob(DDNS.class)
                    .withIdentity("refreshDNSJob", "group-default")
                    .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                    .withIdentity("refreshDNSTrigger", "group-default")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(Integer.parseInt(System.getProperty("refreshIntervalInSeconds")))
                            .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);
            // and start it off
            scheduler.start();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }
}
