package com.ibm.bao.ceshell.cm;

import java.util.ArrayList;
import java.util.List;

public class Locks {
	
	List<LockVO> locks = new ArrayList<LockVO>();
	List<String> loggedInUsers = new ArrayList<String>();
	
	public void addLock(LockVO lock) {
		locks.add(lock);
	}
	
	public void addUser(String user) {
		loggedInUsers.add(user);
	}

	public List<LockVO> getLocks() {
		return locks;
	}

	public List<String> getLoggedInUsers() {
		return loggedInUsers;
	}
	
	public int count() {
		return locks.size();
	}

}
