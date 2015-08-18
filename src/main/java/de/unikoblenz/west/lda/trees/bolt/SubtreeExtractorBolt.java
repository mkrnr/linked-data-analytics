package de.unikoblenz.west.lda.trees.bolt;

import java.io.InvalidObjectException;
import java.util.List;
import java.util.Map;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import de.unikoblenz.west.lda.treeGeneration.RootNode;
import de.unikoblenz.west.lda.treeGeneration.Window;
import jline.internal.Log;

/**
 * This class provides a storm bolt that consumes tree from
 * {@link SubtreeExtractorBolt} and returns a list of subtrees
 * 
 * @author Martin Koerner <info@mkoerner.de>, Olga Zagovora <zagovora@uni-koblenz.de>	
 *
 */
public class SubtreeExtractorBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;
	
	OutputCollector collector;

	public void prepare(@SuppressWarnings("rawtypes") Map conf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	public void execute(Tuple tuple) {
		Object rootNodeObject=tuple.getValue(0);
		if(rootNodeObject.getClass()!=RootNode.class){
			Log.error("tuple does not contain RootNode");
			this.collector.ack(tuple);
			return;
		}

		RootNode rootNode=(RootNode)rootNodeObject;

		Window window=new Window();
		System.out.println("\nTree structure:");
		List<int[]>subtrees=window.extractSubtrees(rootNode);
		//this.collector.emit(tuple, new Values(subtrees));
		System.out.println("Size of list: "+subtrees.size());

		this.collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tree"));
	}

}
