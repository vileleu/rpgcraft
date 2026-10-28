package fr.jeunesauvage;

import org.bukkit.scheduler.BukkitTask;

public class DataTask<T>
{
    private T			data;
    private BukkitTask	task;

    public DataTask(T data, BukkitTask task) {
        this.data = data;
        this.task = task;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public void cancel() {
        if (task != null)
            task.cancel();
        task = null;
    }
}