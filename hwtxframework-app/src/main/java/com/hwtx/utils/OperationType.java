package com.hwtx.utils;

/**
 * 记录操作类型
 * 
 */
public enum OperationType {
	/**
	 * @author LC 调整
	 */
	Modify {
		@Override
		public String toString() {
			return "Modify";
		}
	},
	/**
	 * @author LC 新建
	 */
	Create {
		@Override
		public String toString() {
			return "Create";
		}
	},
	/**
	 * @author LC 删除
	 */
	Delete {
		@Override
		public String toString() {
			return "Delete";
		}
	},
	/**
	 * @author LC 空事件
	 */
	Null {
		@Override
		public String toString() {
			return "Null";
		}
	},
	/**
	 * @author LC 重命名
	 */
	Rename {
		@Override
		public String toString() {
			return "Rename";
		}
	},
	/**
	 * @author LC 没用到
	 */
	Move {
		@Override
		public String toString() {
			return "Move";
		}
	}
}