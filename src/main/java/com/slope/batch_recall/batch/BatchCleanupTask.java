package com.slope.batch_recall.batch;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Allow batch to start (nor failure nor not starting) blocked by a previous job
 * cleanup STARTED jobs and set to FAILED (not completed or failed (eg: Ctrl+C))
 */
@Slf4j
@Component
@AllArgsConstructor
public class BatchCleanupTask {

  private JobExplorer jobExplorer;
  private JobRepository jobRepository;

  @PostConstruct
  private void failedJobUpdated() throws Exception {
    log.info("cleaning up jobs before running batch");
    // jobs in progress [JobExecution: id=15, version=1, startTime=2025-05-21T16:16:10.469004, 
    // endTime=null, lastUpdated=2025-05-21T16:16:10.473961, status=STARTED, 
    // exitStatus=exitCode=UNKNOWN;exitDescription=, job=[JobInstance: id=2, version=0, Job=[product-job]]
    // , jobParameters=[{}]]
    var executions = jobExplorer.findRunningJobExecutions("product-job");
    var currentTime = LocalDateTime.now();
    for (var job : executions) {
      job.setStatus(BatchStatus.ABANDONED);
      job.setEndTime(currentTime);
      jobRepository.update(job); // update step

      for (var step : job.getStepExecutions()) {
        step.setStatus(BatchStatus.FAILED); // required to set to FAILED to run next 
        step.setEndTime(currentTime);
        jobRepository.update(step); // update step
      }
    }
  }

  // @PostConstruct
  private void testAsyncVirtualThreads() {
    var task = new SimpleAsyncTaskExecutor();
    task.setVirtualThreads(true); // results are the same if true and false for async
    
    var counter = new AtomicInteger(1);
    for (int i=0; i<10000; i++) {
      task.execute(() -> {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          if (counter.get() >= 9990) 
            log.info("hello task nb {}", counter.getAndIncrement());
          else counter.incrementAndGet();
        }
      });
    }

    task.close();
  }
}
