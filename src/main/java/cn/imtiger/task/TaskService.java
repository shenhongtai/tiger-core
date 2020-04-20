package cn.imtiger.task;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.imtiger.util.data.DatetimeUtil;
import cn.imtiger.util.data.ValidateUtil;

/**
 * 定时任务服务
 * @author shen_hongtai
 * @date 2019-10-5
 */
public class TaskService {
	private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
	
	/**
	 * 任务服务与线程列表映射
	 */
	private Map<ScheduledExecutorService, List<String>> serviceMap;	
	
	/**
	 * 线程对象引用映射
	 */
	private Map<String, Thread> threadMap;
	
	/**
	 * 线程执行周期映射
	 */
	private Map<String, Integer> threadDelayMap;
	
	/**
	 * 线程执行时间映射
	 */
	private Map<String, Date> threadStartTimeMap;
	
	/**
	 * 定时任务服务初始化
	 */
	public TaskService() {
		// 初始化映射Map
		serviceMap = new HashMap<>();
		threadMap = new HashMap<>();
		threadDelayMap = new HashMap<>();
		threadStartTimeMap = new HashMap<>();
		
		// 启动线程状态巡检任务
		runInspection();
	}
	
	/**
	 * 任务启动
	 * @param task
	 * @return
	 */
	public boolean start(AbstractTask task) {
		if (!check(task)) {
			return false;
		}
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		serviceMap.put(service, new ArrayList<String>());
		run(service, task);
		return true;
	}

	/**
	 * 任务批量启动
	 * @param taskList
	 */
	public void batchStart(List<AbstractTask> taskList) {
		if (taskList == null || taskList.size() == 0) {
			logger.error("Task list could not be null");
			return;
		}
		
		ScheduledExecutorService service = Executors.newScheduledThreadPool(taskList.size());
		serviceMap.put(service, new ArrayList<String>());
		for (AbstractTask task : taskList) {
			if (!check(task)) {
				logger.error("Task[" + taskList.indexOf(task) + "] occured an error");
				continue;
			}
			run(service, task);
		}
	}

	/**
	 * 任务暂停
	 * @param taskName
	 */
	public void pause(String taskName) {
		Thread thread = threadMap.get(taskName);
		if (!thread.getState().equals(State.RUNNABLE)) {
			logger.warn("Task[" + taskName + "] could not be paused");
		} else {
			try {
				thread.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("Task[" + taskName + "] has failed to be paused");
				logger.error(e.getMessage());
			}
		}
	}
	
	/**
	 * 任务批量暂停
	 * @param taskNameList
	 */
	public void batchPause(List<String> taskNameList) {
		for (String taskName : taskNameList) {
			this.pause(taskName);
		}
	}

	/**
	 * 任务继续
	 * @param taskName
	 */
	public void notify(String taskName) {
		Thread thread = threadMap.get(taskName);
		if (!thread.getState().equals(State.WAITING)) {
			logger.warn("Task[" + taskName + "] could not be notify");
		} else {
			thread.notify();
		}
	}
	
	/**
	 * 任务批量继续
	 * @param taskNameList
	 */
	public void batchNotify(List<String> taskNameList) {
		for (String taskName : taskNameList) {
			this.notify(taskName);
		}
	}
	
	/**
	 * 任务中止
	 * @param taskName
	 */
	public void stop(String taskName) {
		Thread thread = threadMap.get(taskName);
		if (thread.isInterrupted()) {
			logger.warn("Task[" + taskName + "] has already been stopped");
		} else {
			thread.interrupt();
		}
	}
	
	/**
	 * 任务批量中止
	 * @param taskNameList
	 */
	public void batchStop(List<String> taskNameList) {
		for (String taskName : taskNameList) {
			this.stop(taskName);
		}
	}
	
	/**
	 * 获取当前任务信息列表
	 * @return
	 */
	public List<Map<String, Object>> getTaskList() {
		List<Map<String, Object>> list = new ArrayList<>();
		Iterator<Entry<String, Thread>> iterator = threadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, Thread> entry = iterator.next();
            Map<String, Object> map = new HashMap<>();
            map.put("name", entry.getKey());
            map.put("delay", threadDelayMap.get(entry.getKey()));
            map.put("state", entry.getValue().getState());
            map.put("start_time", DatetimeUtil.formatDateTime(threadStartTimeMap.get(entry.getKey()), "yyyy-MM-dd HH:mm:ss"));
            list.add(map);
        }
        return list;
	}
	
	/**
	 * 任务信息校验
	 * @param task
	 * @return
	 */
	private boolean check(AbstractTask task) {
		if (task == null) {
			logger.error("Task could not be null");
			return false;
		}		
		if (ValidateUtil.isNull(task.getTaskName().trim())) {
			logger.error("Task name could not be null or blank");
			return false;
		}		
		if (task.getDelay() == null) {
			logger.error("Task delay could not be null");
			return false;
		}
		return true;
	}
	
	/**
	 * 任务执行
	 * @param service
	 * @param task
	 */
	private void run(ScheduledExecutorService service, AbstractTask task) {
		Thread thread = new Thread(task.getTaskName()) {
			@Override
			public void run() {
				task.run();				
			}
		};
		service.scheduleWithFixedDelay(thread, 1, task.getDelay(), TimeUnit.SECONDS);
		threadMap.put(task.getTaskName(), thread);
		threadDelayMap.put(task.getTaskName(), task.getDelay());
		threadStartTimeMap.put(task.getTaskName(), new Date());
		serviceMap.get(service).add(task.getTaskName());
		logger.info("Task[" + task.getTaskName() + "] is running");
	}
	
	/**
	 * 服务状态巡检
	 */
	private void inspection() {
		Iterator<Entry<ScheduledExecutorService, List<String>>> iterator = serviceMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ScheduledExecutorService, List<String>> entry = iterator.next();
			ScheduledExecutorService service = entry.getKey();
			List<String> list = entry.getValue();
			boolean unusedService = true;
			for (String threadName : list) {
				if (!threadMap.get(threadName).isInterrupted()) {
					unusedService = false;
				}
			}
			if (unusedService) {
				service.shutdownNow();
			}
		}
	}
	
	/**
	 * 服务状态巡检任务执行
	 */
	private void runInspection() {
		String taskName = "服务状态巡检任务";
		Thread thread = new Thread(taskName) {
			@Override
			public void run() {
				inspection();
			}
		};
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleWithFixedDelay(thread, 1, 60, TimeUnit.SECONDS);
		logger.info("Task[" + taskName + "] is running");
	}
}
