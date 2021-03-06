package uchile;
 
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
 
public class AirlineDelayByStartTimeMapper extends MapReduceBase implements
  Mapper<LongWritable, Text, Text, DoubleWritable> {
	
public static boolean isNumeric(String str) {  
	  try {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe) {  
	    return false;  
	  }  
	  return true;  
}
 
 public void map(LongWritable key, Text value,
   OutputCollector<Text, DoubleWritable> output, Reporter reporter)
   throws IOException {
 
  // Split the input line based on comma
  String[] pieces = value.toString().split(",");
 
  // Delayed 0 for on time or NA, 1 for delayed
  int delayed = 0;
 
  // Get the origin which is the 17 field in the input line
  String origin = pieces[16];
 
  if (isNumeric(pieces[4])
    && isNumeric(pieces[5]) && isNumeric(pieces[13])) {
 
   // 5 DepTime actual departure time (local, hhmm)
   // 6 CRSDepTime scheduled departure time (local, hhmm)
   int actualDepTime = Integer.parseInt(pieces[4]);
   int scheduledDepTime = Integer.parseInt(pieces[5]);
   int airTime = Integer.parseInt(pieces[13]);
 
   // if the flight has been delated
   if (actualDepTime > scheduledDepTime) {
    delayed = 1;
   }
  }
 
  // Send the Origin and the delayed status to the reducer for aggregation
  // ex., (ORD, 1)
  output.collect(new Text(origin), new DoubleWritable(delayed));
 
 }
}