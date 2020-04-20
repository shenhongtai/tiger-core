package cn.imtiger.task;

/**
 * 定时任务抽象类
 * @author shen_hongtai
 * @date 2019-10-5
 */
public abstract class AbstractTask {
	/**
	 * 任务名称
	 */
	private String taskName;
	/**
	 * 任务间隔时间(秒)
	 */
	private Integer delay;
	
	/**
	 * 默认构造方法
	 */
	public AbstractTask() {
		this.init();
	}
	
	/**
	 * 任务初始化，设置任务名称及间隔时间
	 */
	public abstract void init();

	/**
	 * 任务执行逻辑
	 */
	public abstract void run();

	public String getTaskName() {
		return taskName;
	}

	public Integer getDelay() {
		return delay;
	}

	protected void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	protected void setDelay(Integer delay) {
		this.delay = delay;
	}
}
