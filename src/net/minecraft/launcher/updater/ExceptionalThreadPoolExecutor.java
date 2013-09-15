package net.minecraft.launcher.updater;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.minecraft.launcher.Launcher;

public class ExceptionalThreadPoolExecutor extends ThreadPoolExecutor {
	public class ExceptionalFutureTask<T> extends FutureTask<T> {

		public ExceptionalFutureTask(Callable<T> callable) {
			super(callable);
		}

		public ExceptionalFutureTask(Runnable runnable, T result) {
			super(runnable, result);
		}

		@Override
		protected void done() {
			try {
				get();
			} catch (Throwable t) {
				Launcher.getInstance().println(
						"Unhandled exception in executor " + this, t);
			}
		}
	}

	public ExceptionalThreadPoolExecutor(int threadCount) {
		super(threadCount, threadCount, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);

		if ((t == null) && ((r instanceof Future)))
			try {
				Future<?> future = (Future<?>) r;
				if (future.isDone())
					future.get();
			} catch (CancellationException ce) {
				t = ce;
			} catch (ExecutionException ee) {
				t = ee.getCause();
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
			}
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new ExceptionalFutureTask<T>(callable);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new ExceptionalFutureTask<T>(runnable, value);
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.ExceptionalThreadPoolExecutor JD-Core Version:
 * 0.6.2
 */