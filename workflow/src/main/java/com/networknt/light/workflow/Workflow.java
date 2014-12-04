package java.com.networknt.light.workflow;

import java.util.Map;

/**
 * Created by husteve on 11/7/2014.
 */
public interface Workflow {

    Map<String, Object> startProcessAsync(Map<String, Object> data, Map<String, Object> user);

    Map<String, Object> startProcess(Map<String, Object> data, Map<String, Object> user);

    Map<String, Object> createTask(Map<String, Object> data, Map<String, Object> process, Map<String,Object> user);

    void assignTask(Map<String, Object> data, Map<String, Object> task);

    void reassignTask(Map<String, Object> data, Map<String, Object> task);

    void changePriority(long taskId, int priority);

    void cancelTask(long taskId);

    void completeTaskAsync(long taskId, String result, String userId, String action, TypesafeMap completionData);

    void completeTask(long taskId, String result, String userId, String action, TypesafeMap completionData);

    long waitForEvent(AsyncProcessEvent event, Date fireTimestamp);

    void cancelEvent(AsyncProcessEvent event);

    void resumeProcessAsync(long processId, String resumeProcessLabel, String status);

    void completeProcess(long processId, String result);

    void logAuditEntry(AuditLogBuilder builder);

    void logAuditEntryTx(AuditLogBuilder builder);

    boolean terminateProcess(OtisProcessInfoVO processInfo);

}
