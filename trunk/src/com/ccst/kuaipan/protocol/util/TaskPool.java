package com.ccst.kuaipan.protocol.util;

import java.util.ArrayList;


public class TaskPool {

	public static interface TaskInterface
	{
		abstract public void excute();
		abstract public boolean isEqual(Object object);
	}

    public ArrayList<TaskInterface> mTaskList = new ArrayList<TaskInterface>();
    public int mMaxTaskSize = 3;
    public int mRunningTaskNumber = 0; 
    
    public TaskPool(int maxSize)
    {
    	mMaxTaskSize = maxSize;
    }
    
    synchronized public void addTask(TaskInterface task)
    {
    	mTaskList.add(task);
    	doTask();
    }
    
    synchronized public void removeTask(Object object)
    {
    	for(int i = mTaskList.size() - 1; i >= 0 ; --i)
    	{
    		if(mTaskList.get(i).isEqual(object))
    		{
    			mTaskList.remove(i);
    			if(i < mRunningTaskNumber)
    				--mRunningTaskNumber;
    		}
    	}
    	if (mRunningTaskNumber < 0)
    	    mRunningTaskNumber = 0;
    	doTask();
    }
    
    public void doTask() 
    {
    	if(mRunningTaskNumber < mMaxTaskSize)
    	{
    		for(int i = mRunningTaskNumber; i < Math.min(mMaxTaskSize, mTaskList.size()); ++i)
    		{
    			mTaskList.get(i).excute();
    			++mRunningTaskNumber;
    		}
    	}
    }
    
    public void clear()
    {
        mTaskList.clear();
        mRunningTaskNumber = 0;
    }
}
