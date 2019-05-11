import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Sample {

    public static Set<String>[] readPartitions(String partitionFile){
        ArrayList<Set<String>> partitionList = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(FileSystems.getDefault().getPath(partitionFile))));
            String line;
            //String[] linesplit;
            HashSet<String> tempPartition;
            while ((line = reader.readLine()) != null) {
                tempPartition = new HashSet<>(Arrays.asList(line.split(",",0)));
                /*linesplit = line.split(",",0);
                for(String s:linesplit)
                    tempPartition.add(s);*/
                partitionList.add(tempPartition);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return partitionList.toArray(new Set[partitionList.size()]);
    }

    public static void writePartitions(String partitionFile,Set<String>[] partitions){
        try{
            Path partitionPath = FileSystems.getDefault().getPath(partitionFile);
            Files.deleteIfExists(partitionPath);
            for(Set<String> strings:partitions) {
                for (String s : strings)
                    Files.write(partitionPath, (s + ",").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                Files.write(partitionPath,("\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /*public static double averageTotalMilano(String inputFile) {
        double sum = 0;
        Long min = Long.MAX_VALUE;
        Long max = Long.MIN_VALUE;
        Long time;
        Path input = FileSystems.getDefault().getPath(inputFile);
        try {
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\s",8);
                //value, sms in[3], sms out[4], call in[5], call out[6], internet [7]
                sum += linesplit[3].isEmpty() ? 0 : Double.parseDouble(linesplit[3]);
                sum += linesplit[4].isEmpty() ? 0 : Double.parseDouble(linesplit[4]);
                sum += linesplit[5].isEmpty() ? 0 : Double.parseDouble(linesplit[5]);
                sum += linesplit[6].isEmpty() ? 0 : Double.parseDouble(linesplit[6]);
                time = Long.parseLong(linesplit[1]);
                if(max < time)
                    max = time;
                if(min > time)
                    min = time;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        //600000 for Milano dataset, 3600 for Traceset dataset
        return sum / (((max - min) / 600000) + 1);
    }*/

    public static void metisFromDandelionAreaToArea(String dandelionFile,String metisFile){
        try {
            //n is number of vertices, m is number of edges, fmt is 3 digit boolean :vertices size-vertices weight-edges weight, ncon is number of vertex weights
            int m = 0;

            //read dandelion file into treemap
            TreeMap<String,HashMap<String,Double>> treeMap = new TreeMap<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(FileSystems.getDefault().getPath(dandelionFile))));
            String line;
            String[] linesplit;
            HashMap<String,Double> tempHashMap;
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\s+");
                //since self loops are assigned exclusively as vertices weights, it is fine to add twice
                if(treeMap.containsKey(linesplit[1]))
                    //if ever updated, change merge to put
                    treeMap.get(linesplit[1]).merge(linesplit[2],Double.parseDouble(linesplit[3]),Double::sum);
                else {
                    tempHashMap = new HashMap<>();
                    tempHashMap.put(linesplit[2],Double.parseDouble(linesplit[3]));
                    treeMap.put(linesplit[1],tempHashMap);
                }
                if(treeMap.containsKey(linesplit[2]))
                    //if ever updated, change merge to put
                    treeMap.get(linesplit[2]).merge(linesplit[1],Double.parseDouble(linesplit[3]),Double::sum);
                else {
                    tempHashMap = new HashMap<>();
                    tempHashMap.put(linesplit[1],Double.parseDouble(linesplit[3]));
                    treeMap.put(linesplit[2],tempHashMap);
                }
            }

            //log vertices indices and count edges
            int index = 1;
            HashMap<String,Integer> verticesIndicesMap = new HashMap<>();
            for(Map.Entry<String,HashMap<String,Double>> entry:treeMap.entrySet()){
                verticesIndicesMap.put(entry.getKey(),index);
                index ++;
                //count edges, case where vertex has self loop or not
                if(entry.getValue().containsKey(entry.getKey()))
                    m += entry.getValue().size() - 1;
                else
                    m += entry.getValue().size();
            }
            m /= 2;

            //write treemap into metis format
            Path metisPath = FileSystems.getDefault().getPath(metisFile);
            Files.deleteIfExists(metisPath);

            //no vertex weight info
            //header line
            Files.write(metisPath,(treeMap.size() + " " + m + " 001\n").getBytes(),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            //n lines
            for(Map.Entry<String,HashMap<String,Double>> entry0:treeMap.entrySet()){
                for(Map.Entry<String,Double> entry1:entry0.getValue().entrySet())
                    //check for self loop
                    if(!entry0.getKey().equals(entry1.getKey()))
                        Files.write(metisPath,(verticesIndicesMap.get(entry1.getKey()) + " " + (long)(entry1.getValue() * 1L) + " ").getBytes(),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                Files.write(metisPath,("\n").getBytes(),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }

            /*//self loop as vertex weight
            //header line
            Files.write(metisPath,(treeMap.size() + " " + m + " 011 1\n").getBytes(),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            //n lines
            for(Map.Entry<String,HashMap<String,Double>> entry0:treeMap.entrySet()){
                //self loop as vertex weight
                Files.write(metisPath,(entry0.getValue().containsKey(entry0.getKey()) ? (long)(entry0.getValue().get(entry0.getKey()) * 1L) + " " : "0 ").getBytes(),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                //remove self loop
                entry0.getValue().remove(entry0.getKey());
                for(Map.Entry<String,Double> entry1:entry0.getValue().entrySet())
                    Files.write(metisPath,(verticesIndicesMap.get(entry1.getKey()) + " " + (long)(entry1.getValue() * 1L) + " ").getBytes(),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                Files.write(metisPath,("\n").getBytes(),StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }*/
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void metisToPartition(String metisFile,String outputFile,TreeSet<String> idSet){
        //find the number of partitions
        int max = Integer.MIN_VALUE;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(FileSystems.getDefault().getPath(metisFile))));
            String line;
            while ((line = reader.readLine()) != null)
                if(max < Integer.parseInt(line))
                    max = Integer.parseInt(line);
        } catch (IOException e){
            e.printStackTrace();
        }
        HashSet<String>[] partitions = new HashSet[max + 1];
        for(int i = 0;i < partitions.length;i ++)
            partitions[i] = new HashSet<>();
        //read partitions
        try {
            String[] array = idSet.toArray(new String[idSet.size()]);
            int index = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(FileSystems.getDefault().getPath(metisFile))));
            String line;
            while ((line = reader.readLine()) != null){
                partitions[Integer.parseInt(line)].add(array[index]);
                index += 1;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        //write partitions
        try{
            Path output = FileSystems.getDefault().getPath(outputFile);
            Files.deleteIfExists(output);
            for(HashSet<String> strings:partitions) {
                for (String s : strings)
                    Files.write(output, (s + ",").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                Files.write(output, ("\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /*public static void sampleMilanoLogNormalParameters(String inputFile, String outputFile, boolean hasHeaderLine){
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try{
            Files.deleteIfExists(output);
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            long time;
            long max = Long.MIN_VALUE;
            long min = Long.MAX_VALUE;
            HashMap<String,HashMap<Long,Double>> map = new HashMap<>();
            long tempKey;
            double tempValue;
            HashMap<Long,Double> tempMap;
            if(hasHeaderLine)
                reader.readLine();
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\t",8);
                //time
                time = Long.parseLong(linesplit[1]);
                if(time > max)
                    max = time;
                if(time < min)
                    min = time;
                tempKey = Long.parseLong(linesplit[1]);
                //value, sms in[3], sms out[4], call in[5], call out[6], internet [7]
                //beware, in Milano a cellID at a time can have multiple records
                tempValue = 0d;
                tempValue += linesplit[3].isEmpty() ? 0 : Double.parseDouble(linesplit[3]);
                tempValue += linesplit[4].isEmpty() ? 0 : Double.parseDouble(linesplit[4]);
                tempValue += linesplit[5].isEmpty() ? 0 : Double.parseDouble(linesplit[5]);
                tempValue += linesplit[6].isEmpty() ? 0 : Double.parseDouble(linesplit[6]);
                if(map.containsKey(linesplit[0]))
                    map.get(linesplit[0]).merge(tempKey,tempValue,Double::sum);
                else {
                    tempMap = new HashMap<>();
                    tempMap.put(tempKey,tempValue);
                    map.put(linesplit[0],tempMap);
                }
            }
            //number of recording 10mins
            time = (max - min) / 600000 + 1;
            //write to file
            Iterator<Map.Entry<String,HashMap<Long,Double>>> iterator = map.entrySet().iterator();
            Map.Entry<String,HashMap<Long,Double>> entry;
            DistributionLogNormal tempLogNormal;
            while(iterator.hasNext()) {
                entry = iterator.next();
                tempLogNormal = new DistributionLogNormal(entry.getValue().values(),time);
                Files.write(output, (entry.getKey() + "," + tempLogNormal.toString() + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }*/

    /*public static void sampleMilanoNormalParameters(String inputFile, String outputFile, boolean hasHeaderLine){
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try{
            Files.deleteIfExists(output);
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            long time;
            long max = Long.MIN_VALUE;
            long min = Long.MAX_VALUE;
            HashMap<String,HashMap<Long,Double>> map = new HashMap<>();
            long tempKey;
            double tempValue;
            HashMap<Long,Double> tempMap;
            if(hasHeaderLine)
                reader.readLine();
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\t",8);
                //time
                time = Long.parseLong(linesplit[1]);
                if(time > max)
                    max = time;
                if(time < min)
                    min = time;
                tempKey = Long.parseLong(linesplit[1]);
                //value, sms in[3], sms out[4], call in[5], call out[6], internet [7]
                //beware, in Milano a cellID at a time can have multiple records
                tempValue = 0d;
                tempValue += linesplit[3].isEmpty() ? 0 : Double.parseDouble(linesplit[3]);
                tempValue += linesplit[4].isEmpty() ? 0 : Double.parseDouble(linesplit[4]);
                tempValue += linesplit[5].isEmpty() ? 0 : Double.parseDouble(linesplit[5]);
                tempValue += linesplit[6].isEmpty() ? 0 : Double.parseDouble(linesplit[6]);
                if(map.containsKey(linesplit[0]))
                    map.get(linesplit[0]).merge(tempKey,tempValue,Double::sum);
                else {
                    tempMap = new HashMap<>();
                    tempMap.put(tempKey,tempValue);
                    map.put(linesplit[0],tempMap);
                }
            }
            //number of recording 10mins
            time = (max - min) / 600000 + 1;
            //write to file
            Iterator<Map.Entry<String,HashMap<Long,Double>>> iterator = map.entrySet().iterator();
            Map.Entry<String,HashMap<Long,Double>> entry;
            DistributionNormal tempNormal;
            while(iterator.hasNext()) {
                entry = iterator.next();
                tempNormal = new DistributionNormal(entry.getValue().values(),time);
                Files.write(output, (entry.getKey() + "," + tempNormal.toString() + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }*/

    public static void extractDandelionArea(String inputFile, String outputFile, Set<String> idSet) {
        if(idSet == null)
            return;
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try {
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Files.deleteIfExists(output);
            String line;
            String [] linesplit;
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\s");
                if (idSet.contains(linesplit[0]))
                    Files.write(output, (line + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }



    /*public static void extractTracesetBaseStations(String inputFile, String outputFile, int randomStep){
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try {
            Files.deleteIfExists(output);
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            //String[] linesplit;
            Random random = new Random();
            int count = random.nextInt(randomStep);
            while ((line = reader.readLine()) != null) {
                if(count > 0)
                    count -= 1;
                else {
                    Files.write(output,(line + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                    count = random.nextInt(randomStep);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }*/

    /*public static void sampleTracesetLogNormalParameters(String inputFile, String outputFile, boolean hasHeaderLine){
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try{
            Files.deleteIfExists(output);
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            long time;
            long max = Long.MIN_VALUE;
            long min = Long.MAX_VALUE;
            HashMap<String, ArrayList<Double>> map = new HashMap<>();
            double tempValue;
            ArrayList<Double> tempArray;
            if(hasHeaderLine)
                reader.readLine();
            while ((line = reader.readLine()) != null) {
                linesplit = line.split(",");
                //time
                time = Long.parseLong(linesplit[1]);
                if(time > max)
                    max = time;
                if(time < min)
                    min = time;
                //value, packets [3] or bytes [4]
                tempValue = Double.parseDouble(linesplit[4]);
                if(map.containsKey(linesplit[0]))
                    map.get(linesplit[0]).add(tempValue);
                else {
                    tempArray = new ArrayList<>();
                    tempArray.add(tempValue);
                    map.put(linesplit[0],tempArray);
                }
            }
            //number of recording hours
            time = (max - min) / 3600 + 1;
            //write to file
            Iterator<Map.Entry<String,ArrayList<Double>>> iterator = map.entrySet().iterator();
            Map.Entry<String,ArrayList<Double>> entry;
            DistributionLogNormal tempLogNormal;
            while(iterator.hasNext()) {
                entry = iterator.next();
                tempLogNormal = new DistributionLogNormal(entry.getValue(),time);
                Files.write(output, (entry.getKey() + "," + tempLogNormal.toString() + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }*/

    /*//too much computation & memory, do not use
    public static void sampleTracesetDistanceMatrix(String inputFile, String outputFile, boolean hasHeaderLine){
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try{
            Files.deleteIfExists(output);
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            HashMap<String,double[]> map = new HashMap<>();
            if(hasHeaderLine)
                reader.readLine();
            while ((line = reader.readLine()) != null) {
                linesplit = line.split(",");
                map.put(linesplit[0],new double[]{Double.parseDouble(linesplit[1]),Double.parseDouble(linesplit[2])});
            }
            //String[][] matrix = new String[map.size() + 1][map.size() + 1];
            //matrix[0][0] = "";
            Files.write(output, (",").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            Iterator<Map.Entry<String,double[]>> iterator;
            Map.Entry<String,double[]> entry;
            iterator = map.entrySet().iterator();
            String[] reference = new String[map.size() + 1];
            reference[0] = "";
            for(int i = 1;i < map.size() + 1;i ++) {
                entry = iterator.next();
                //matrix[0][i] = iterator.next().getKey();
                Files.write(output, (entry.getKey() + ",").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                reference[i] = entry.getKey();
            }
            Files.write(output, ("\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            iterator = map.entrySet().iterator();
            for(int i = 1;i < map.size() + 1;i ++) {
                entry = iterator.next();
                //matrix[i][0] = entry.getKey();
                Files.write(output, (entry.getKey() + ",").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                for (int j = 1; j < map.size() + 1; j++)
                    //matrix[i][j] = Double.toString(distance(entry.getValue(),map.get(matrix[0][j])));
                    Files.write(output, (distance(entry.getValue(),map.get(reference[j])) + ",").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                Files.write(output, ("\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }*/

    /*static double distance(double[] A,double[] B){
        double sqrAB = 0;
        for(int i = 0;i < B.length;i ++)
            sqrAB += (B[i] - A[i]) * (B[i] - A[i]);
        return Math.sqrt(sqrAB);
    }*/

    /*public static String[][] mapToMatrix(TreeSet<String> idSet,HashMap<Pair<String>,Double> map) {
        String[] idArray = idSet.toArray(new String[idSet.size()]);
        String[][] matrix = new String[idSet.size() + 1][idSet.size() + 1];
        matrix[0][0] = "";
        for(int i = 1;i <= idSet.size();i ++) {
            matrix[i][0] = matrix[0][i] = idArray[i - 1];
        }
        if(map == null)
            for(int i = 1;i <= idSet.size();i ++)
                for(int j = i;j <= idSet.size();j ++)
                    matrix[i][j] = matrix[j][i] = "0";
        else
            for(int i = 1;i <= idSet.size();i ++)
                for(int j = i;j <= idSet.size();j ++)
                    matrix[i][j] = matrix[j][i] = map.containsKey(new Pair<>(matrix[i][0],matrix[0][j])) ? map.get(new Pair<>(matrix[i][0],matrix[0][j])).toString() : "0";
        return matrix;
    }

    //create an undirected symmetric 3D matrix file where each 2D matrix has size n+1 x n+1 where 1st index is the time and cell name
    public static void extractToMatrix(String inputFile, String outputFile) {
        if(inputFile == null || inputFile.equals("") || outputFile == null || outputFile.equals(""))
            return;
        TreeMap<Long,HashMap<Pair<String>,Double>> timeMap = new TreeMap<>();
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(input)));
            Files.deleteIfExists(output);
            String line;
            String [] split;
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            long time;
            TreeSet<String> idSet = new TreeSet<>();
            HashMap<Pair<String>,Double> interactionMap;
            while ((line = reader.readLine()) != null) {
                split = line.split("\\s");
                time = Long.parseLong(split[0]);
                if(max < time)
                    max = time;
                if(min > time)
                    min = time;
                idSet.add(split[1]);
                idSet.add(split[2]);
                if(timeMap.containsKey(time))
                    timeMap.get(time).put(new Pair<>(split[1],split[2]),Double.parseDouble(split[3]));
                else{
                    interactionMap = new HashMap<>();
                    interactionMap.put(new Pair<>(split[1],split[2]),Double.parseDouble(split[3]));
                    timeMap.put(time,interactionMap);
                }
            }
            String[][] matrix;
            for(time = min;time <= max;time += 600000) {
                matrix = mapToMatrix(idSet,timeMap.get(time));
                matrix[0][0] = Long.toString(time);
                for(String[] strings:matrix) {
                    for(String s:strings)
                        Files.write(output,(s + "\t").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                    Files.write(output,("\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                }
                //line seperating different matrices
                Files.write(output,("\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[][][] importMatrix(String file) {
        String[][][] matrix = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(FileSystems.getDefault().getPath(file))));

            ArrayList<String> strings = new ArrayList<>();
            String line;
            String [] split;
            while ((line = reader.readLine()) != null) {
                strings.add(line);
            }
            int size = strings.get(0).split("\t").length;
            matrix = new String[strings.size() / (size + 1)][size][size];
            for(int i = 0;i < strings.size();i += size + 1)
                for(int j = i;j < i + size;j ++) {
                    split = strings.get(j).split("\t");
                    for (int k = 0; k < size; k++)
                        matrix[i / (size + 1)][j % (size + 1)][k] = split[k];
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matrix;
    }*/

    //old summing methods with raw dataset
    //sum the total interaction values
    public static double totalDandelionAreaToArea(String dandelionFile) {
        double sum = 0;
        Path input = FileSystems.getDefault().getPath(dandelionFile);
        try {
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\s");
                sum += Double.parseDouble(linesplit[3]);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return sum;
    }

    //average total, for Dandelion time period is 10mins or 600 000ms
    public static double totalAverageDandelionAreaToArea(String dandelionFile) {
        double sum = 0;
        Long min = Long.MAX_VALUE;
        Long max = Long.MIN_VALUE;
        Long time;
        Path input = FileSystems.getDefault().getPath(dandelionFile);
        try {
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\s");
                sum += Double.parseDouble(linesplit[3]);
                time = Long.parseLong(linesplit[0]);
                if(max < time)
                    max = time;
                if(min > time)
                    min = time;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return sum / (((max - min) / 600000) + 1);
    }

    /*//new method with matrix dataset
    public static double totalAverageDandelionAreaToArea(String inputFile) {
        double sum = 0;
        String[][][] matrix = importMatrix(inputFile);
        for(int i = 0;i < matrix.length;i ++)
            for(int j = 1;j < matrix[i].length;j ++)
                for(int k = j;k < matrix[i][j].length;k ++)
                    sum += Double.parseDouble(matrix[i][j][k]);
        return sum / matrix.length;
    }*/

    public static void extractDandelionAreaToArea(String inputFile, String outputFile, Set<String> idSet) {
        if(idSet == null)
            return;
        Path input = FileSystems.getDefault().getPath(inputFile);
        Path output = FileSystems.getDefault().getPath(outputFile);
        try {
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Files.deleteIfExists(output);
            String line;
            String [] linesplit;
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\s");
                if (idSet.contains(linesplit[1]) && idSet.contains(linesplit[2]))
                    Files.write(output, (line + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static HashSet<String> collectIdsDandelionAreaToArea(String dandelionAreaToAreaFile) {
        HashSet<String> ids = new HashSet<>();
        Path input = FileSystems.getDefault().getPath(dandelionAreaToAreaFile);
        try {
            InputStream in = Files.newInputStream(input);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            String [] linesplit;
            while ((line = reader.readLine()) != null) {
                linesplit = line.split("\\s");
                ids.add(linesplit[1]);
                ids.add(linesplit[2]);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return ids;
    }

    public static Set<String> castRectangleToSet(int startingId,int horizontalRange,int verticalRange,int matrixSize) {
        //check if the rectangle is out of bound
        if(startingId % matrixSize + horizontalRange > matrixSize || startingId / matrixSize + verticalRange > matrixSize)
            return null;
        if(horizontalRange < 0) {
            startingId -= horizontalRange;
            horizontalRange = - horizontalRange;
        }
        if(verticalRange < 0) {
            startingId -= verticalRange * matrixSize;
            verticalRange = - verticalRange;
        }
        HashSet<String> set = new HashSet<>();
        for (int i = startingId;i < startingId + horizontalRange;i ++)
            for (int j = 0;j < verticalRange;j ++)
                set.add(Integer.toString(i + j * matrixSize));
        return set;
    }

    public static TreeMap<Long,HashMap<Pair<String>,Double>> constructBenchmarkDandelionAreaToArea(String filepath){
        TreeMap<Long,HashMap<Pair<String>,Double>> dataset = new TreeMap<>();
        ArrayList<String> data = readFromFile(filepath);
        HashMap<Pair<String>,Double> tempmap;
        String[] split;
        for(String s:data) {
            split = s.split("\\s");
            /*if ever updated, change merge to put
            tempmap = new HashMap<>();
            tempmap.put(new Pair<>(split[1],split[2]),Double.parseDouble(split[3]));
            dataset.merge(Long.parseLong(split[0]),tempmap,(oldmap,newmap) -> {
                for(Map.Entry<Pair<String>,Double> entry:newmap.entrySet())
                    oldmap.merge(entry.getKey(),entry.getValue(),Double::sum);
                return oldmap;
            });*/
            if(dataset.containsKey(Long.parseLong(split[0]))) {
                if(dataset.get(Long.parseLong(split[0])).containsKey(new Pair<>(split[1], split[2]))){
                    //undirected and directed graph will have different result on merge and put
                    //System.out.println("Duplicate Entries");
                    dataset.get(Long.parseLong(split[0])).merge(new Pair<>(split[1], split[2]), Double.parseDouble(split[3]), Double::sum);
                }
                else
                    dataset.get(Long.parseLong(split[0])).put(new Pair<>(split[1], split[2]), Double.parseDouble(split[3]));
            }
            else {
                tempmap = new HashMap<>();
                tempmap.put(new Pair<>(split[1],split[2]),Double.parseDouble(split[3]));
                dataset.put(Long.parseLong(split[0]),tempmap);
            }
        }
        return dataset;
    }

    public static ArrayList<String> readFromFile(String filepath) {
        ArrayList returnList = new ArrayList<>();
        //Path path = FileSystems.getDefault().getPath(filepath);
        try{
            //InputStream in = Files.newInputStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(FileSystems.getDefault().getPath(filepath))));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0)
                    returnList.add(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return returnList;
    }

    public static void generateLogNormalDistributionAreaToArea(Set<String> idSet,Set<Integer> timeSet,String syntheticFile){
        try {
            double mu,sigma;
            Random random = new Random();
            Path syntheticPath = FileSystems.getDefault().getPath(syntheticFile);
            Files.deleteIfExists(syntheticPath);
            Iterator<String> iterator = idSet.iterator();
            while(iterator.hasNext()){
                String id1 = iterator.next();
                for (String id2 : idSet) {
                    mu = random.nextInt(10);
                    sigma = random.nextInt(10);
                    for (Integer time : timeSet)
                        Files.write(syntheticPath, (time + "\t" + id1 + "\t" + id2 + "\t" + Math.exp(random.nextGaussian() * sigma + mu) + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                }
                iterator.remove();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /*public static void test(String filepath){
        HashMap<Pair<String>,Integer> countMap = new HashMap<>();
        HashMap<Pair<String>,HashMap<Long,Double>> map = new HashMap<>();
        //Long min = Long.MAX_VALUE,max = Long.MIN_VALUE,time;
        HashSet<Long> time = new HashSet<>();
        String[] tempsplit;
        //HashMap<Long,Double> tempmap;
        for(String s:readFromFile(filepath)) {
            tempsplit = s.split("\t");

            if(countMap.containsKey(new Pair<>(tempsplit[1],tempsplit[2]))){
                countMap.put(new Pair<>(tempsplit[1],tempsplit[2]),(countMap.get(new Pair<>(tempsplit[1],tempsplit[2])) + 1));
            }
            else {
                countMap.put(new Pair<>(tempsplit[1],tempsplit[2]),1);
            }

            tempmap = new HashMap<>();
            tempmap.put(Long.parseLong(tempsplit[0]),Double.parseDouble(tempsplit[3]));
            map.merge(new Pair<>(tempsplit[1],tempsplit[2]),tempmap,(oldmap,newmap) -> {
                oldmap.putAll(newmap);
                return oldmap;
            });

            time.add(Long.parseLong(tempsplit[0]));
            *//*time = Long.parseLong(tempsplit[0]);
            if(max < time)
                max = time;
            if(min > time)
                min = time;*//*
        }
        //time = (max - min) / 600000 + 1;

        int max = 0;
        Pair<String> maxkey = null;
        for(Map.Entry<Pair<String>,Integer> entry:countMap.entrySet()){
            if(max < entry.getValue()){
                max = entry.getValue();
                maxkey = entry.getKey();
            }
        }
        System.out.println(maxkey + "\t" + max);

        System.out.print(maxkey + "\t");
        for(long t:time) {
            if(map.get(maxkey).containsKey(t))
                System.out.print(map.get(maxkey).get(t) + "\t");
            else
                System.out.print("0\t");
        }
    }*/

}
