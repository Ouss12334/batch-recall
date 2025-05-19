package com.slope.batch_recall.service;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SimpleJobListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    log.info("before job begin");
    JobExecutionListener.super.beforeJob(jobExecution);
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.info("after job begin");
    JobExecutionListener.super.afterJob(jobExecution);
  }

}
