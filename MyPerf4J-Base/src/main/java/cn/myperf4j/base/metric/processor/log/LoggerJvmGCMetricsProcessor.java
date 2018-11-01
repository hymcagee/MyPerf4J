package cn.myperf4j.base.metric.processor.log;

import cn.myperf4j.base.metric.JvmGCMetrics;
import cn.myperf4j.base.metric.formatter.JvmGCMetricsFormatter;
import cn.myperf4j.base.metric.formatter.impl.DefaultJvmGCMetricsFormatter;
import cn.myperf4j.base.metric.processor.AbstractJvmGCMetricsProcessor;
import cn.myperf4j.base.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/8/25
 */
public class LoggerJvmGCMetricsProcessor extends AbstractJvmGCMetricsProcessor {

    private ConcurrentHashMap<Long, List<JvmGCMetrics>> metricsMap = new ConcurrentHashMap<>(8);

    private JvmGCMetricsFormatter metricsFormatter = new DefaultJvmGCMetricsFormatter();

    @Override
    public void beforeProcess(long processId, long startMillis, long stopMillis) {
        metricsMap.put(processId, new ArrayList<JvmGCMetrics>(1));
    }

    @Override
    public void process(JvmGCMetrics metrics, long processId, long startMillis, long stopMillis) {
        List<JvmGCMetrics> metricsList = metricsMap.get(processId);
        if (metricsList != null) {
            metricsList.add(metrics);
        } else {
            Logger.error("LoggerJvmGCMetricsProcessor.process(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }

    @Override
    public void afterProcess(long processId, long startMillis, long stopMillis) {
        List<JvmGCMetrics> metricsList = metricsMap.remove(processId);
        if (metricsList != null) {
            logger.logAndFlush(metricsFormatter.format(metricsList, startMillis, stopMillis));
        } else {
            Logger.error("LoggerJvmGCMetricsProcessor.afterProcess(" + processId + ", " + startMillis + ", " + stopMillis + "): metricsList is null!!!");
        }
    }
}
