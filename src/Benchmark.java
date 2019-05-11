import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Benchmark {

    private TreeMap<Long,HashMap<Pair<String>,Double>> dataset;
    //private String[][][] matrix;

    public Benchmark(TreeMap<Long,HashMap<Pair<String>,Double>> dataset){
        this.dataset = dataset;
    }

    //old constructor for raw dataset
    /*public Benchmark(String filepath) {
        this.dataset = new TreeMap<>();
        ArrayList<String> data = readFromFile(filepath);
        HashMap<Pair<String>,Double> tempmap;
        String[] split;
        for(String s:data) {
            split = s.split("\\s");
            tempmap = new HashMap<>();
            tempmap.merge(new Pair<>(split[1],split[2]),Double.parseDouble(split[3]),Double::sum);
            this.dataset.merge(Long.parseLong(split[0]),tempmap,(oldmap,newmap) -> {
                newmap.putAll(oldmap);
                return newmap;
            });
        }
        //this.serverCapacities = serverCapacities;
    }*/

    //new constructor for matrix dataset, matrix is bs
    /*public Benchmark(String matrixFile) {
        String[][][] times = SampleDandelionAreaToArea.importMatrix(matrixFile);
        this.dataset = new TreeMap<>();
        HashMap<Pair<String>,Double> map;
        for(String[][] matrix:times) {
            map = new HashMap<>();
            for (int i = 1; i < matrix.length; i++)
                for (int j = i; j < matrix[0].length; j++)
                    if (!matrix[i][j].equals("0"))
                        map.put(new Pair<>(matrix[i][0], matrix[0][j]), Double.parseDouble(matrix[i][j]));
            this.dataset.put(Long.parseLong(matrix[0][0]), map);
        }
    }*/

    /*public ArrayList<String> readFromFile(String filepath) {
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
    }*/

    //draw partition for Milano dataset
    public static void drawPartitions(Set<String>[] partitions,int scale) {
        if(scale < 1)
            scale = 1;
        JFrame window = new JFrame();
        window.add(new MyCanvas(partitions,scale));
        window.setVisible(true);
    }

    public static String printResultVertically(TreeMap<Long,double[]> result) {
        String returnString = "";
        for(Map.Entry<Long, double[]> entry:result.entrySet()) {
            returnString += entry.getKey();
            for(int i = 0;i < entry.getValue().length;i ++)
                returnString += "\t" + entry.getValue()[i];
            returnString += "\n";
        }
        return returnString;
    }

    public static String printResultHorizontally(TreeMap<Long,double[]> result,String methodName) {
        String returnString = "Time";
        for(long time:result.keySet())
            returnString += "\t" + time;
        returnString += "\n" + methodName;
        for(double[] doubles:result.values())
            returnString += "\t" + doubles[0];
        return returnString;
    }

    public static String printPartitions(Set<String>[] partitions) {
        String returnString = "";
        for(int i = 0;i < partitions.length;i ++) {
            returnString += i + "\t:\t";
            for(String s:partitions[i])
                returnString += s + ",";
            returnString += "\n";
        }
        return returnString;
    }

    public TreeMap<Long,double[]> printWorkload(Set<String>[] partitions) {
        TreeMap<Long, double[]> workloadMap = new TreeMap<>();
        double[] workload;
        for(Map.Entry<Long, HashMap<Pair<String>, Double>> entry0:this.dataset.entrySet()) {
            workload = new double[partitions.length];
            Arrays.fill(workload,0);
            for(Map.Entry<Pair<String>,Double> entry1:entry0.getValue().entrySet())
                if(entry1.getKey().id1.equals(entry1.getKey().id2))
                    for(int i = 0;i < partitions.length;i ++)
                        if(partitions[i].contains(entry1.getKey().id1)) {
                            workload[i] += entry1.getValue();
                            break;
                        }
            workloadMap.put(entry0.getKey(),workload);
        }
        return workloadMap;
    }

    /*public TreeMap<Long,double[]> printWorkload(Set<String>[] partitions) {
        TreeMap<Long, double[]> workloadMap = new TreeMap<>();
        double[] workload;
        //HashMap<String,Integer> map = new HashMap<>();
        //for(int i = 1;i < this.matrix[0][0].length;i ++)
            //map.put(this.matrix[0][0][i],i);
        for(String[][] entry0:this.matrix) {
            workload = new double[partitions.length];
            Arrays.fill(workload,0);
            for(int i = 1;i < entry0.length;i ++)
                for(int j = i;j < entry0[0].length;j ++)
                    if(!entry0[i][j].equals("0"))
                        for(int k = 0;k < partitions.length;k ++)
                            if(partitions[k].contains(entry0[i][0]) && partitions[k].contains(entry0[0][j]))
                                workload[k] += Double.parseDouble(entry0[i][j]);
            workloadMap.put(Long.parseLong(entry0[0][0]),workload);
        }
        return workloadMap;
    }*/

    public TreeMap<Long,double[]> testOvercapacity(double[] serverCapacities, Set<String>[] partitions) {
        TreeMap<Long, double[]> overcapacityMap = new TreeMap<>();
        double[] overcapacity;
        HashMap<String,Integer> vertexServerMap = new HashMap<>();
        for(int i = 0;i < Math.min(serverCapacities.length,partitions.length);i ++)
            for(String s:partitions[i])
                vertexServerMap.put(s,i);
        for(Map.Entry<Long, HashMap<Pair<String>, Double>> entry0:this.dataset.entrySet()) {
            overcapacity = new double[serverCapacities.length];
            Arrays.fill(overcapacity,0);
            for(Map.Entry<Pair<String>,Double> entry1:entry0.getValue().entrySet())
                if(vertexServerMap.containsKey(entry1.getKey().id1) && vertexServerMap.containsKey(entry1.getKey().id2) && vertexServerMap.get(entry1.getKey().id1) == vertexServerMap.get(entry1.getKey().id2))
                    overcapacity[vertexServerMap.get(entry1.getKey().id1)] += entry1.getValue();
            for(int i = 0;i < serverCapacities.length;i ++)
                overcapacity[i] = overcapacity[i] > serverCapacities[i] ? overcapacity[i] - serverCapacities[i] : 0;
            overcapacityMap.put(entry0.getKey(),overcapacity);
        }
        return overcapacityMap;
    }

    public TreeMap<Long,double[]> testCrossServers(Set<String>[] partitions) {
        TreeMap<Long,double[]> edgeMap = new TreeMap<>();
        double crossServer;
        HashMap<String,Integer> vertexServerMap = new HashMap<>();
        for(int i = 0;i < partitions.length;i ++)
            for(String s:partitions[i])
                vertexServerMap.put(s,i);
        for(Map.Entry<Long,HashMap<Pair<String>,Double>> entry0:this.dataset.entrySet()) {
            crossServer = 0;
            for(Map.Entry<Pair<String>,Double> entry1:entry0.getValue().entrySet())
                if(vertexServerMap.containsKey(entry1.getKey().id1) && vertexServerMap.containsKey(entry1.getKey().id2) && vertexServerMap.get(entry1.getKey().id1) != vertexServerMap.get(entry1.getKey().id2))
                    crossServer += entry1.getValue();
            edgeMap.put(entry0.getKey(),new double[]{crossServer});
        }
        return edgeMap;
    }

    public TreeMap<Long,double[]> testUnassigned(Set<String>[] partitions) {
        TreeMap<Long, double[]> unassignedMap = new TreeMap<>();
        double unassigned;
        HashSet<String> assigned = new HashSet<>();
        for(Set<String> partition:partitions)
            for(String s:partition)
                assigned.add(s);
        for(Map.Entry<Long,HashMap<Pair<String>,Double>> entry0:this.dataset.entrySet()) {
            unassigned = 0;
            for(Map.Entry<Pair<String>,Double> entry1:entry0.getValue().entrySet())
                if(!assigned.contains(entry1.getKey().id1) || !assigned.contains(entry1.getKey().id2))
                    unassigned += entry1.getValue();
            unassignedMap.put(entry0.getKey(),new double[]{unassigned});
        }
        return unassignedMap;
    }

    public TreeMap<Long,double[]> total(TreeMap<Long,double[]>[] tests){
        TreeMap<Long,double[]> returnTotal = new TreeMap<>();
        for(TreeMap<Long,double[]> test:tests){
            Iterator<Map.Entry<Long,double[]>> iterator = test.entrySet().iterator();
            Map.Entry<Long,double[]> entry;
            double sum;
            while(iterator.hasNext()) {
                entry = iterator.next();
                if(returnTotal.containsKey(entry.getKey()))
                    for(double d:entry.getValue())
                        returnTotal.get(entry.getKey())[0] += d;
                else{
                    sum = 0;
                    for(double d:entry.getValue())
                        sum += d;
                    returnTotal.put(entry.getKey(),new double[]{sum});
                }
            }
        }
        return returnTotal;
    }

    public double total(TreeMap<Long,double[]> tests){
        double returnTotal = 0;
        Iterator<Map.Entry<Long,double[]>> iterator = tests.entrySet().iterator();
        Map.Entry<Long,double[]> entry;
        while(iterator.hasNext()) {
            entry = iterator.next();
            for(double d:entry.getValue())
                returnTotal += d;
        }
        return returnTotal;
    }

    /*public TreeMap<Long,double[]> testTotal(double[] serverCapacities, Set<String>[] partitions){
        TreeMap<Long,double[]> totalMap = testOvercapacity(serverCapacities,partitions);
        Iterator<Map.Entry<Long,double[]>> iterator = testCrossServers(partitions).entrySet().iterator();
        Map.Entry<Long,double[]> entry;
        while(iterator.hasNext()){
            entry = iterator.next();
            totalMap.merge(entry.getKey(),entry.getValue(),(double[] oldDoubles,double[] newDoubles) -> {
                double temp = 0d;
                for(double d:oldDoubles)
                    temp += d;
                for(double d:newDoubles)
                    temp += d;
                return new double[]{temp};
            });
        }
        iterator = testUnassigned(partitions).entrySet().iterator();
        while(iterator.hasNext()){
            entry = iterator.next();
            totalMap.merge(entry.getKey(),entry.getValue(),(double[] oldDoubles,double[] newDoubles) -> {
                double temp = 0d;
                for(double d:oldDoubles)
                    temp += d;
                for(double d:newDoubles)
                    temp += d;
                return new double[]{temp};
            });
        }
        return totalMap;
    }

    public double total(double[] serverCapacities, Set<String>[] partitions){
        double total = 0;
        for(Map.Entry<Long,double[]> entry:testOvercapacity(serverCapacities,partitions).entrySet())
            for(double d:entry.getValue())
                total += d;
        for(Map.Entry<Long,double[]> entry:testCrossServers(partitions).entrySet())
            for(double d:entry.getValue())
                total += d;
        for(Map.Entry<Long,double[]> entry:testUnassigned(partitions).entrySet())
            for(double d:entry.getValue())
                total += d;
        return total;
    }*/

}