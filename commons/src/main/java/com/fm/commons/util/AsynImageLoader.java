package com.fm.commons.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.BitmapLoader;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

@SuppressLint("HandlerLeak")
public class AsynImageLoader {
	private static final String TAG = "AsynImageLoader";
	private static final String BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
	private static final String FILE_DIR = BASE_DIR + "/com/mu/future/";

	// 缓存下载过的图片的Map
	private Map<String, SoftReference<Bitmap>> caches;
	// 任务队列
	private List<Task> taskQueue;
	private boolean isRunning = false;
	private BitmapLoader bitmapLoader;

	public AsynImageLoader() {
		// 初始化变量
		caches = new HashMap<String, SoftReference<Bitmap>>();
		taskQueue = new ArrayList<AsynImageLoader.Task>();
		bitmapLoader = BeanFactory.getBean(BitmapLoader.class);
		// 启动图片下载线程
		isRunning = true;
		new Thread(runnable).start();
	}

	/**
	 * 
	 * @param imageView
	 *            需要延迟加载图片的对象
	 * @param url
	 *            图片的URL地址
	 * @param resId
	 *            图片加载过程中显示的图片资源
	 */
	public void showImageAsyn(ImageView imageView, String url, int resId) {
		imageView.setTag(url);
		Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageView, resId));
		if (bitmap == null) {
			imageView.setImageResource(resId);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	public Bitmap loadImageAsyn(String path, ImageCallback callback) {
		if (null == path || "".equals(path))
			return null;
		// 判断缓存中是否已经存在该图片
		if (caches.containsKey(path)) {
			// 取出软引用
			SoftReference<Bitmap> rf = caches.get(path);
			// 通过软引用，获取图片
			Bitmap bitmap = rf.get();
			// 如果该图片已经被释放，则将该path对应的键从Map中移除掉
			if (bitmap == null) {
				caches.remove(path);
			} else {
				// 如果图片未被释放，直接返回该图片
				Log.i(TAG, "return image in cache" + path);
				return bitmap;
			}
		} else {
			if (isSdCard()) {
				Bitmap bitmap = readBySdCard(path);
				if (bitmap != null)
					return bitmap;
			}
			// 如果缓存中不常在该图片，则创建图片下载任务
			Task task = new Task();
			task.path = path;
			if (callback != null)
				task.callback = callback;
			Log.i(TAG, "new Task ," + path);
			if (!taskQueue.contains(task)) {
				taskQueue.add(task);
				// 唤醒任务下载队列
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}

		// 缓存中没有图片则返回null
		return null;
	}

	/**
	 * 
	 * @param imageView
	 * @param resId
	 *            图片加载完成前显示的图片资源ID
	 * @return
	 */
	private ImageCallback getImageCallback(final ImageView imageView, final int resId) {
		return new ImageCallback() {
			@Override
			public void loadImage(String path, Bitmap bitmap) {
				if (null == path) {
					imageView.setImageResource(resId);
				} else if (imageView.getTag().toString().equals(path)) {
					imageView.setImageBitmap(bitmap);
				} else {
					imageView.setImageResource(resId);
				}
			}
		};
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 子线程中返回的下载完成的任务
			Task task = (Task) msg.obj;
			// 调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
			if (isSdCard()) {
				checkFilePath();
				saveFileOnPath(task);
			}
			task.callback.loadImage(task.path, task.bitmap);
		}
	};

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			while (isRunning) {
				// 当队列中还有未处理的任务时，执行下载任务
				while (taskQueue.size() > 0) {
					// 获取第一个任务，并将之从任务队列中删除
					Task task = taskQueue.remove(0);
					// 将下载的图片添加到缓存
					task.bitmap = bitmapLoader.load(task.path);
					caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));
					if (handler != null) {
						// 创建消息对象，并将完成的任务添加到消息对象中
						Message msg = handler.obtainMessage();
						msg.obj = task;
						// 发送消息回主线程
						handler.sendMessage(msg);
					}
				}

				// 如果队列为空,则令线程等待
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	// 回调接口
	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap);
	}

	private boolean isSdCard() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	private void checkFilePath() {
		File path = new File(FILE_DIR);
		if (!path.exists())
			path.mkdirs();
	}

	private void saveFileOnPath(Task task) {
		String path = task.path;
		if (null == path || "".equals(path))
			return;
		String fpath = path.substring(path.lastIndexOf("/") + 1, path.length());
		String dir = FILE_DIR + fpath;
		File file = new File(dir);
		if (!file.exists()) {
			try {
				file.createNewFile();
				System.out.println("saving bitmap = " + fpath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				task.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				BufferedOutputStream bufferedOs = new BufferedOutputStream(fos);
				bufferedOs.flush();
				bufferedOs.close();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Bitmap readBySdCard(String path) {
		if (null == path || "".equals(path))
			return null;
		String fpath = path.substring(path.lastIndexOf("/") + 1, path.length());
		File file = new File(FILE_DIR);
		File[] files = file.listFiles();
		if (files == null)
			return null;
		if (files.length == 0)
			return null;
		for (int i = 0; i < files.length; i++) {
			File child = files[i];
			String childPath = child.getName().substring(child.getName().lastIndexOf("/") + 1,
					child.getName().length());
			if (!childPath.equals(fpath))
				continue;
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeFile(FILE_DIR + childPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("reading bitmap form sdCard = " + bitmap);
			return bitmap;
		}
		return null;
	}

	class Task {
		// 下载任务的下载路径
		String path;
		// 下载的图片
		Bitmap bitmap;
		// 回调对象
		ImageCallback callback;

		@Override
		public boolean equals(Object o) {
			Task task = (Task) o;
			if (null == task.path || "".equals(task.path))
				return false;
			return task.path.equals(path);
		}
	}
}
