import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static String inputFile = "D:\\extractedMItoMI-2013-11-01.txt";
    private static String outputFile0 = "output0.txt";
    private static String outputFile1 = "output1.txt";
    private static String outputFile2 = "output2.txt";
    private static String debugFile = "debug.txt";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");

    public static void writeToFile(String filepath, String data, boolean append){
        try{
            FileWriter fileWriter = new FileWriter(new File(filepath),append);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            System.out.print(data);
            bufferedWriter.write(data);
            bufferedWriter.close();
        } catch (IOException e){
            System.out.println(e);
        }
    }

    public static void writelnToFile(String filepath, String data, boolean append){
        try{
            FileWriter fileWriter = new FileWriter(new File(filepath),append);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            System.out.println(data);
            bufferedWriter.write(data + "\n");
            bufferedWriter.close();
        } catch (IOException e){
            System.out.println(e);
        }
    }

    private static void testSet0(String inputFile,String outputFile,String debugFile,double[] serverCapacities,int[] serverNumbers) {
        Benchmark benchmark = new Benchmark(Sample.constructBenchmarkDandelionAreaToArea(inputFile));
        Set<String>[] partitions;
        double[] servers;

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"RAND",true);
        writelnToFile(debugFile,"RAND",true);
        PartitionRandom partitionRandom = new PartitionRandom(inputFile);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionRandom.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"BC",true);
        writelnToFile(debugFile,"BC",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);            
            for(int serverNumber:serverNumbers){
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                PartitionMECGeoAverage partitionMECGeoAverage = new PartitionMECGeoAverage(inputFile);
                partitions = partitionMECGeoAverage.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        PartitionNormalMu partitionNormalMu;

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.1",true);
        writelnToFile(debugFile,"Normal Mu 0.1",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.1);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.2",true);
        writelnToFile(debugFile,"Normal Mu 0.2",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.2);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.3",true);
        writelnToFile(debugFile,"Normal Mu 0.3",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.3);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.4",true);
        writelnToFile(debugFile,"Normal Mu 0.4",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.4);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.5",true);
        writelnToFile(debugFile,"Normal Mu 0.5",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.5);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.6",true);
        writelnToFile(debugFile,"Normal Mu 0.6",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.6);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.7",true);
        writelnToFile(debugFile,"Normal Mu 0.7",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.7);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.8",true);
        writelnToFile(debugFile,"Normal Mu 0.8",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.8);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Normal Mu 0.9",true);
        writelnToFile(debugFile,"Normal Mu 0.9",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMu = new PartitionNormalMu(inputFile);
                partitionNormalMu.setTheta(0.9);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMu.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"VT",true);
        writelnToFile(debugFile,"VT",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                PartitionNormalMuAdaptiveTheta partitionNormalMuAdaptiveTheta = new PartitionNormalMuAdaptiveTheta(inputFile);
                partitionNormalMuAdaptiveTheta.setStep(0.05);
                partitions = partitionNormalMuAdaptiveTheta.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        /*writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Improved VT",true);
        writelnToFile(debugFile,"Improved VT",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                PartitionNormalMuAdaptiveThetaProgressive partitionNormalMuAdaptiveThetaProgressive = new PartitionNormalMuAdaptiveThetaProgressive(inputFile);
                partitionNormalMuAdaptiveThetaProgressive.setStep(0.05);
                partitionNormalMuAdaptiveThetaProgressive.setSection(5);
                partitions = partitionNormalMuAdaptiveThetaProgressive.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);*/
    }

    private static void testSet1(String inputFile,String outputFile,String debugFile,double[] serverCapacities,int[] serverNumbers){
        Benchmark benchmark = new Benchmark(Sample.constructBenchmarkDandelionAreaToArea(inputFile));
        Set<String>[] partitions;
        double[] servers;

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"RAND",true);
        writelnToFile(debugFile,"RAND",true);
        PartitionRandom partitionRandom = new PartitionRandom(inputFile);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionRandom.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"BC",true);
        writelnToFile(debugFile,"BC",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                PartitionMECGeoAverage partitionMECGeoAverage = new PartitionMECGeoAverage(inputFile);
                partitions = partitionMECGeoAverage.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        PartitionNormalMuContiguity partitionNormalMuContiguity;

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.1",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.1",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.1);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.2",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.2",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.2);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.3",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.3",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.3);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.4",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.4",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.4);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.5",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.5",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);            
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.5);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.6",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.6",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);            
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.6);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.7",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.7",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.7);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.8",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.8",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.8);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu 0.9",true);
        writelnToFile(debugFile,"Contiguity Normal Mu 0.9",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                partitionNormalMuContiguity.setTheta(0.9);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);

        writelnToFile(debugFile,"Start: " + sdf.format(new Date()),true);
        writeToFile(outputFile,"Contiguity Normal Mu Adaptive Theta",true);
        writelnToFile(debugFile,"Contiguity Normal Mu Adaptive Theta",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                PartitionNormalMuAdaptiveThetaContiguity partitionNormalMuAdaptiveThetaContiguity = new PartitionNormalMuAdaptiveThetaContiguity(inputFile);
                partitionNormalMuAdaptiveThetaContiguity.setStep(0.05);
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = partitionNormalMuAdaptiveThetaContiguity.partition(serverCapacity,serverNumber);
                writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
        writelnToFile(debugFile,"End: " + sdf.format(new Date()),true);
    }

    private static void testSetPartitionFile(String inputFile,String outputFile,String debugFile,String partitionFile,double[] serverCapacities,int[] serverNumbers) {
        Benchmark benchmark = new Benchmark(Sample.constructBenchmarkDandelionAreaToArea(inputFile));
        Set<String>[] partitions;
        double[] servers;

        writeToFile(outputFile,"Metis10PartitionsWEdgeWeight",true);
        writelnToFile(debugFile,"Metis10PartitionsWEdgeWeight",true);
        for(int serverNumber:serverNumbers)
            writeToFile(outputFile,"\t" + serverNumber,true);
        writeToFile(outputFile,"\n",true);
        for(double serverCapacity:serverCapacities){
            writeToFile(outputFile,Double.toString(serverCapacity),true);
            for(int serverNumber:serverNumbers){
                servers = new double[serverNumber];
                Arrays.fill(servers,serverCapacity);
                partitions = Sample.readPartitions(partitionFile);
                writeToFile(outputFile,"\t" + Double.toString(benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)}))),true);
                writelnToFile(debugFile,"Vertices",true);
                writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                writelnToFile(debugFile,"Workload",true);
                writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
            }
            writeToFile(outputFile,"\n",true);
        }
    }

    private static void testSetCanvas(String inputFile,double[] serverCapacities,int[] serverNumbers) {
    }

    private static void testSetTheta(String inputFile,String outputFile,String debugFile,double[] serverCapacities,int[] serverNumbers){
        Benchmark benchmark = new Benchmark(Sample.constructBenchmarkDandelionAreaToArea(inputFile));
        Set<String>[] partitions;
        double[] servers;
        PartitionNormalMuContiguity partitionNormalMuContiguity;

        for(double serverCapacity:serverCapacities) {
            for (int serverNumber : serverNumbers) {
                writeToFile(outputFile,serverNumber + "@" + serverCapacity,true);
                for(double theta = 0;theta <= 1;theta += 0.05) {
                    writeToFile(outputFile,"\t" + theta,true);
                }
                writeToFile(outputFile,"\nTotal",true);
                for(double theta = 0;theta <= 1;theta += 0.05) {
                    writelnToFile(debugFile, "Start: " + sdf.format(new Date()), true);
                    partitionNormalMuContiguity = new PartitionNormalMuContiguity(inputFile);
                    partitionNormalMuContiguity.setTheta(theta);
                    servers = new double[serverNumber];
                    Arrays.fill(servers,serverCapacity);
                    partitions = partitionNormalMuContiguity.partition(serverCapacity,serverNumber);
                    writelnToFile(debugFile,"Partitioned: " + sdf.format(new Date()),true);
                    writeToFile(outputFile,"\t" + benchmark.total(benchmark.total(new TreeMap[]{benchmark.testOvercapacity(servers,partitions),benchmark.testCrossServers(partitions),benchmark.testUnassigned(partitions)})),true);
                    writelnToFile(debugFile,"Vertices",true);
                    writelnToFile(debugFile,benchmark.printPartitions(partitions),true);
                    writelnToFile(debugFile,"Workload",true);
                    writelnToFile(debugFile,benchmark.printResultVertically(benchmark.printWorkload(partitions)),true);
                    writelnToFile(debugFile, "End: " + sdf.format(new Date()), true);
                }
                writeToFile(outputFile,"\n",true);
            }
        }

    }

    public static void main(String[] args) {
        //extract sample
        //Sample.sampleTracesetLogNormalParameters(args[0],args[1],true);
        //Sample.sampleTracesetDistanceMatrix(args[0],args[1],true);
        //Sample.extractTracesetBaseStations(args[0],args[1],266);
        //Sample.sampleMilanoNormalParameters(args[0],args[1],false);
        //Sample.extractDandelionAreaToArea(args[0],args[1],Sample.castRectangleToSet(4040,20,20,100));
        //Sample.metisFromDandelionAreaToArea(args[0],args[1]);
        //Sample.metisToPartition(args[0],args[1],new TreeSet<>(Sample.castRectangleToSet(4040,20,20,100)));
        /*HashSet<Integer> timeSet = new HashSet<>();
        for(int i = 0;i < 50;i ++)
            timeSet.add(i);
        Sample.generateLogNormalDistributionAreaToArea(Sample.castRectangleToSet(4040,20,20,100),timeSet,args[0]);*/
        //Benchmark.drawPartitions(Sample.readPartitions(args[0]),50);
        Sample.test(inputFile);

        //main program for partitioning and evaluation
        /*try{
            Files.deleteIfExists(FileSystems.getDefault().getPath(outputFile0));
            Files.deleteIfExists(FileSystems.getDefault().getPath(outputFile1));
            Files.deleteIfExists(FileSystems.getDefault().getPath(outputFile2));
            Files.deleteIfExists(FileSystems.getDefault().getPath(debugFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //double total = SampleDandelionAreaToArea.total(inputFile);
        double averageTotal = Sample.totalAverageDandelionAreaToArea(inputFile);
        //writelnToFile(outputFile0,Double.toString(totalAverageDandelionAreaToArea),true);
        writelnToFile(debugFile,Double.toString(averageTotal),true);

        //test set
        int[] serverNumbers = new int[]{5,10,15,20,25};
        double[] kappaFractions = new double[]{0.01,0.05,0.1,0.15,0.2};
        double[] serverCapacities = new double[kappaFractions.length];
        for(int i = 0;i < kappaFractions.length;i ++)
            serverCapacities[i] = averageTotal * kappaFractions[i];
        testSet0(inputFile,outputFile0,debugFile,serverCapacities,serverNumbers);
        testSet1(inputFile,outputFile1,debugFile,serverCapacities,serverNumbers);
        //testSet2(inputFile,outputFile1,debugFile,serverCapacities,serverNumbers,Math.sqrt(2));
        //testSet2(inputFile,outputFile2,debugFile,serverCapacities,serverNumbers,3);
        //testSetTheta(inputFile,outputFile0,debugFile,serverCapacities,serverNumbers);*/

        /*String metisFile = "D:\\metisSampledMItoMI\\sampledwvwMItoMI-2013-11-01.txt.part.10.partitions";
        testSetPartitionFile(inputFile,outputFile0,debugFile,metisFile,serverCapacities,serverNumbers);*/
    }

}