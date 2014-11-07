package com.hwtx.utils;

import java.nio.file.Path;
import java.nio.file.WatchKey;

/**
 * 记录{@link WatchKey}及事件的状态类
 */
public class KeyState {
	/**
	 * 事件发生的路径或者{@link WatchKey}对应的路径
	 */
	public Path path;
	/**
	 * {@link WatchKey}上一步操作的路径或者重命名事件中的重命名后的名称
	 */
	public Path exPath;
	/**
	 * 事件的类型
	 */
	public OperationType opType = OperationType.Null;
	/**
	 * {@link WatchKey}的等级或者引发事件的{@link WatchKey}的等级
	 */
	public int level;
	/**
	 * 事件发生的事件
	 */
	public long opTime = -1;

	/**
	 * @param path
	 *            事件发生的路径或者{@link WatchKey}对应的路径
	 * @param level
	 *            {@link WatchKey}的等级或者引发事件的{@link WatchKey}的等级
	 */
	public KeyState(Path path, int level) {
		this.path = path;
		this.level = level;
	}

	/**
	 * @param path
	 *            事件发生的路径或者{@link WatchKey}对应的路径
	 * @param opType
	 *            操作的类型
	 * @param level
	 *            {@link WatchKey}的等级或者引发事件的{@link WatchKey}的等级
	 */
	public KeyState(Path path, OperationType opType, int level) {
		this.path = path;
		this.level = level;
		this.opType = opType;
	}

	/**
	 * @param path
	 *            事件发生的路径或者{@link WatchKey}对应的路径
	 * @param opType
	 *            操作的类型
	 * @param level
	 *            {@link WatchKey}的等级或者引发事件的{@link WatchKey}的等级
	 * @param opTime
	 *            事件发生的时间
	 */
	public KeyState(Path path, OperationType opType, int level, long opTime) {
		super();
		this.path = path;
		this.opType = opType;
		this.level = level;
		this.opTime = opTime;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("opType=" + opType);
		sb.append("\topTime=" + opTime);
		sb.append("\tlevel=" + level);
		if (path != null)
			sb.append("\tpath=" + path.normalize());
		if (exPath != null)
			sb.append("\texPath=" + exPath.normalize());

		return sb.toString();
	}
}