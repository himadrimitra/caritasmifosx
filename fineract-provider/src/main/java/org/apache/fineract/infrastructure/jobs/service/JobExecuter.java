package org.apache.fineract.infrastructure.jobs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.springframework.stereotype.Component;

@Component
public class JobExecuter {

    public static final String ENTITY_COLLECTION = "entityCollection";
    public static final String EXCEPTION_BUILDER = "exceptionBuilder";

    public <V> String executeJob(List<V> baseList, final JobRunner<List<V>> jobRunner) {
        final StringBuilder sb = new StringBuilder();
        if (!baseList.isEmpty()) {
            int numberOfThreads = getNumberOfThreads();
            int listSize = baseList.size();
            if (numberOfThreads <= 1 || numberOfThreads > listSize) {
                jobRunner.runJob(baseList, sb);
            } else {
                int subListSize = listSize / numberOfThreads;
                List<StringBuilder> bufferes = new ArrayList<>(numberOfThreads);
                try {
                    List<Thread> threads = new ArrayList<>();
                    List<List<V>> subLists = splitList(baseList, subListSize, numberOfThreads);
                    for (List<V> subList : subLists) {
                        final StringBuilder threadsb = new StringBuilder();
                        bufferes.add(threadsb);
                        if (subList.size() > 0) {
                            Thread thread = new Thread(new JobRunnerThread<>(jobRunner, subList, threadsb));
                            thread.start();
                            threads.add(thread);
                        }
                    }
                    for (Thread thread : threads) {
                        thread.join();
                    }
                    for (StringBuilder threadsb : bufferes) {
                        sb.append(threadsb.toString());
                    }
                } catch (InterruptedException e) {
                    sb.append("Thread Interrupted for " + jobRunner.toString() + " : " + e.getMessage());
                }
            }

        }
        return sb.toString();
    }

    public <K, V> String executeJob(Map<K, V> baseMap, final JobRunner<Map<K, V>> jobRunner) {
        final StringBuilder sb = new StringBuilder();
        if (!baseMap.isEmpty()) {
            int numberOfThreads = getNumberOfThreads();
            int size = baseMap.size();
            if (numberOfThreads <= 1 || numberOfThreads > size) {
                jobRunner.runJob(baseMap, sb);
            } else {
                TreeMap<K, V> sortedMap = new TreeMap<>(baseMap);
                int subCollectionSize = size / numberOfThreads;
                List<StringBuilder> bufferes = new ArrayList<>(numberOfThreads);
                List<SortedMap<K, V>> mapsList = splitMap(sortedMap, subCollectionSize, numberOfThreads);
                List<Thread> threads = new ArrayList<>(mapsList.size());
                try {
                    for (SortedMap<K, V> map : mapsList) {
                        final StringBuilder threadsb = new StringBuilder();
                        bufferes.add(threadsb);
                        if (!map.isEmpty()) {
                            Thread thread = new Thread(new JobRunnerThread<>(jobRunner, map, threadsb));
                            thread.start();
                            threads.add(thread);
                        }
                    }

                    for (Thread thread : threads) {
                        thread.join();
                    }
                    for (StringBuilder threadsb : bufferes) {
                        sb.append(threadsb.toString());
                    }
                } catch (InterruptedException e) {
                    sb.append("Thread Interrupted for " + jobRunner.toString() + " : " + e.getMessage());
                }
            }

        }
        return sb.toString();
    }

    private class JobRunnerThread<T> implements Runnable {

        final FineractPlatformTenant tenant;
        final T jobDetail;
        final JobRunner<T> jobRunner;
        final StringBuilder sb;

        public JobRunnerThread(final JobRunner<T> jobRunner, final T jobDetail, final StringBuilder sb) {
            this.tenant = ThreadLocalContextUtil.getTenant();
            this.jobRunner = jobRunner;
            this.jobDetail = jobDetail;
            this.sb = sb;
        }

        @Override
        public void run() {
            ThreadLocalContextUtil.setTenant(tenant);
            this.jobRunner.runJob(this.jobDetail, this.sb);
        }

    }

    private <V> List<List<V>> splitList(List<V> list, int size, int MaxSize) {
        int listSize = list.size();
        List<List<V>> parts = new ArrayList<>();
        int partSize = 0;
        for (int i = 0; i < listSize; i += size) {
            partSize++;
            if (i + size < listSize) {
                if (partSize == MaxSize) {
                    parts.add(list.subList(i, listSize));
                    break;
                }
                parts.add(list.subList(i, i + size));
            } else {
                parts.add(list.subList(i, listSize));
            }
        }
        return parts;
    }

    private <K, V> List<SortedMap<K, V>> splitMap(final SortedMap<K, V> map, final int size, int MaxSize) {
        List<K> keys = new ArrayList<>(map.keySet());
        List<SortedMap<K, V>> parts = new ArrayList<>();
        final int listSize = map.size();
        int partSize = 0;
        for (int i = 0; i < listSize; i += size) {
            partSize++;
            if (i + size < listSize) {
                if (partSize == MaxSize) {
                    parts.add(map.tailMap(keys.get(i)));
                    break;
                }
                parts.add(map.subMap(keys.get(i), keys.get(i + size)));
            } else {
                parts.add(map.tailMap(keys.get(i)));
            }
        }
        return parts;
    }

    private int getNumberOfThreads() {
        Map<String, String> jobParams = ThreadLocalContextUtil.getJobParams();
        int numberOfThreads = 1;
        if (jobParams != null && jobParams.containsKey("number-of-threads")) {
            numberOfThreads = Integer.parseInt(jobParams.get("number-of-threads"));
            if (numberOfThreads == 0) {
                numberOfThreads = 1;
            }
        }
        return numberOfThreads;
    }

}
