package uchile;
 
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
 
public class AirlineDelayByStartTimeReducer extends MapReduceBase implements
  Reducer<Text, IntWritable, Text, DoubleWritable> {
 
 private DoubleWritable value = new DoubleWritable();
 
 public void reduce(Text key, Iterator<IntWritable> values,
   OutputCollector<Text, DoubleWritable> output, Reporter reporter)
   throws IOException {
	 
 
  // initialize the total flights and delayed flights
  int totalFlights = 0;
  int delayedFlights = 0;
 
  Text newKey = new Text();
 
  while (values.hasNext()) {
   // Delayed 0 for ontime or NA, 1 for delayed
   int delayed = values.next().get();
   // Increment the totalFlights by 1
   totalFlights = totalFlights + 1;
   // Calculate the number of delayed flights
   delayedFlights = delayedFlights + delayed;
  }
  
  double mean = delayedFlights/totalFlights;
 
  // Create the key ex., ORD\t123
  newKey.set(key.toString() + "\t" + delayedFlights);
 
  // Create the value ex., 150
  //value.set(totalFlights);
  
  value.set(mean);
 
  // Pass the key and the value to Hadoop to write it to the final output
  output.collect(newKey, value);
 }
}