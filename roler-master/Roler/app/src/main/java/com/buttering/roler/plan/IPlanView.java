package com.buttering.roler.plan;

import com.buttering.roler.VO.Role;
import com.buttering.roler.VO.Todo;

import java.util.List;

/**
 * Created by kinamare on 2017-01-15.
 */

public interface IPlanView {
	void setTodoList(List<Todo> todoList);
	void setRoleContent(List<Role> role);
	void setCurrentPosition();
	void hideLoadingBar();
	void showLoadingBar();
	void setTodo(Todo todo);
	void setTodoListId(int id);
	void refreshProgress();
	void moveRoleContent(List<Role> roleList,int movePosition);
	void getTodoList();
	void refreshProgressLast();
}
