package com.demo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZookeeperProSync implements Watcher {
	private static CountDownLatch connectedSempahore = new CountDownLatch(1);
	private static ZooKeeper zk=null;
	private static Stat stat = new Stat();
	
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		//zookpeeper 配置数据存放路径
		String path = "/mydata";
		//连接zookeeper并注册一个默认的监听器
		zk= new ZooKeeper("127.0.0.1:2181", 5000, new ZookeeperProSync());
		//等待zk 连接成功的通知
		connectedSempahore.await();
		//获取path 目录节点的配置数据，并注册默认的监听器
		System.out.println(new String(zk.getData(path, true, stat)));
		Thread.sleep(Integer.MAX_VALUE);
	}

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		if(KeeperState.SyncConnected==event.getState()) {//zk 连接成功通知事件
			if(EventType.None==event.getType()&&null==event.getPath()) {
				connectedSempahore.countDown();
			}else if(event.getType()==EventType.NodeDataChanged) {// zk目录节点数据变化通知事件
				try {
					System.out.println("配置已修改，新值为："+new String(zk.getData(event.getPath(), true, stat)));
				} catch (KeeperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
		
		
		
		
		
	}
	

}
