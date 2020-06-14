/**
 * project3
 */
import java.util.*;
import java.io.*;
//import java.lang.*;

public class project3 {

    //enter the amount of processes here:
    private static final int NUM_PROCESSES = 5;

    public static void main(String[] args) throws FileNotFoundException{
        //create arrays to get processes, arrival times, and service times
        String[] process = new String[NUM_PROCESSES], p2 = new String[NUM_PROCESSES], p3 = new String[NUM_PROCESSES], p4 = new String[NUM_PROCESSES], p5 = new String[NUM_PROCESSES], p6 = new String[NUM_PROCESSES];
        int[] arrival_times = new int[NUM_PROCESSES], a2 = new int[NUM_PROCESSES], a3 = new int[NUM_PROCESSES], a4 = new int[NUM_PROCESSES], a5 = new int[NUM_PROCESSES], a6 = new int[NUM_PROCESSES];
        int[] service_times = new int[NUM_PROCESSES], s2 = new int[NUM_PROCESSES], s3 = new int[NUM_PROCESSES], s4 = new int[NUM_PROCESSES], s5 = new int[NUM_PROCESSES], s6 = new int[NUM_PROCESSES];
        
        //create scanner to read input file
        File file1 = new File("jobs.txt");
        Scanner sc = new Scanner(file1);

        //read file and store info in arrays
        for(int row = 0; row < NUM_PROCESSES; row++)
        {
            process[row] = sc.next();
            arrival_times[row] = sc.nextInt();
            service_times[row] = sc.nextInt();
        }
        //clone p,a,s into all the other arrays
        p2=process.clone();
        p3=process.clone();
        p4=process.clone();
        p5=process.clone();
        p6=process.clone();

        a2=arrival_times.clone();
        a3=arrival_times.clone();
        a4=arrival_times.clone();
        a5=arrival_times.clone();
        a6=arrival_times.clone();

        s2=service_times.clone();
        s3=service_times.clone();
        s4=service_times.clone();
        s5=service_times.clone();
        s6=service_times.clone();

        //call functions to print each scheduling algorithm results based on command line argument
        switch (args[0]) {
            case "FCFS":
                fcfs(process, arrival_times, service_times);
                break;
            case "RR":
                rr(process, arrival_times, service_times);
                break;
            case "SPN":
                spn(process, arrival_times, service_times);
                break;
            case "SRT":
                srt(process, arrival_times, service_times);
                break;
            case "HRRN":
                hrrn(process, arrival_times, service_times);
                break;
            case "FB":
                fb(process, arrival_times, service_times);
                break;
            case "ALL":
                fcfs(process, arrival_times, service_times);
                rr(p2, a2, s2);
                spn(p3, a3, s3);
                srt(p4, a4, s4);
                hrrn(p5, a5, s5);
                fb(p6, a6, s6);
                break;
            default:
                System.out.println("invalid argument entered");
                break;
        }//end switch

        //close scanner
        sc.close();
    }//end main





    
    /**
    *  first come first serve
    */
    private static void fcfs(String[] p, int[] a, int[] s)
    {
        //queue to store the order of the printing, ex: {A, A, A, B, B, B, B, B, C, C...}
        Queue<String> order = new LinkedList<>();

        //print processes row on the top. note: my output is vertical
        System.out.println("FCFS");
        System.out.println("");
        for(int i = 0; i < p.length; i++)
            System.out.print(p[i] + "\t");
        System.out.print("\n");
        
        //perform FCFS algorithm
        //fill ordering queue to determine how to print result
        for(int i = 0; i < p.length; i++)
        {
            //get min arrival_time[i], !problem: getting the index of the min number costs a lot of run time, and its hurting my brain
            int smIndex = smallestNumbersIndex(a);
            //enqueue the "process[i]" "service_time[i]" many times
            for(int x = 0; x < s[smIndex]; x++)
            {
                order.add(p[smIndex]);
            }
            //set "process[i]"s arrival_time to MAX_VALUE
            a[smIndex] = Integer.MAX_VALUE;
        }

        //traverse through whole queue and print results by dequeueing
        while(!order.isEmpty())
        {
            printGraph(order);
            order.remove();
        }
        
        //just a line to make things look nice :)
        System.out.println("--------------------------------------------------------\n");
    }





    /**
    *  round robin
    */
    private static void rr(String[] p, int[] a, int[]s)
    {
        //queue to store the order of the printing, ex: {A, A, A, B, B, B, B, B, C, C...}
        Queue<String> order = new LinkedList<>();

        //print processes row on the top. note: my output is vertical
        System.out.println("RR");
        System.out.println("");
        for(int i = 0; i < p.length; i++)
            System.out.print(p[i] + "\t");
        System.out.print("\n");

        //perform RR algorithm:
        //get total_service_time, which is the total time for all services + the arrival time of the first arrival
        int total_service_time = 0;
        for(int i = 0; i < p.length; i++)
        {
            total_service_time += s[i];
        }
        int firstArrivalTime = minValue(a);
        total_service_time += firstArrivalTime;

        //timer to keep track of the current time of the processor
        int time = 0;
        //loop "total_service_time" amount of times
        while(time < total_service_time)
        {
            //print x blank lines, x being the first arrival time. remember to increment time each time
            if(firstArrivalTime > 0)
            {
                while(firstArrivalTime > 0)
                {
                    System.out.println("");
                    firstArrivalTime--;
                    time++;
                }
            }
            if(firstArrivalTime == time)    //add the first value to the queue
            {
                order.add(p[firstArrivalTime]);
                firstArrivalTime--;
            }

            //print next job
            printGraph(order);
            //dequeue it
            String removedJob = order.remove();
            //increment time
            time++;
            //loop through the arrival time array to check if any processes arrived, if so AND the service time it has is > 0, enqueue it
            for(int arrivalIndex = 0; arrivalIndex < a.length; arrivalIndex++)
            {
                if(a[arrivalIndex] == time && s[arrivalIndex] > 0)
                {
                    order.add(p[arrivalIndex]);
                }
            }
            //decrement the service time of the removed job, maybe create a helper function. if the service time after decrementing is > 0, then add it back into the queue
            decrementServiceTimeofRemovedJob(removedJob, order, p, s);
        }

        //just a line to make things look nice :)
        System.out.println("--------------------------------------------------------\n");
    }





    /**
    *  shortest process next
    */
    private static void spn(String[] p, int[] a, int[] s)
    {
        //queue to store the order of the printing, ex: {A, A, A, B, B, B, B, B, C, C...}
        Queue<String> order = new LinkedList<>();
        //hashmap to keep track of the arriving processes: <name, sevice_time> while the current process is running
        HashMap<String, Integer> arrivingProcessAndServiceTime = new HashMap<String, Integer>();

        //print processes row on the top. note: my output is vertical
        System.out.println("SPN");
        System.out.println("");
        for(int i = 0; i < p.length; i++)
            System.out.print(p[i] + "\t");
        System.out.print("\n");

        //perform SPN algorithm:
        //get total_service_time, which is the total time for all services + the arrival time of the first arrival
        int total_service_time = 0;
        for(int i = 0; i < p.length; i++)
        {
            total_service_time += s[i];
        }
        int firstArrivalTime = minValue(a);
        total_service_time += firstArrivalTime;

        //timer to keep track of the current time of the processor
        int time = 0;
        //loop "total_service_time" amount of times
        while(time < total_service_time)
        {
            //print x blank lines, x being the first arrival time. remember to increment time each time
            if(firstArrivalTime > 0)
            {
                while(firstArrivalTime > 0)
                {
                    System.out.println("");
                    firstArrivalTime--;
                    time++;
                }
            }
            if(firstArrivalTime == time)    //add the first value to the queue
            {
                //loop service time of the first process many times 
                for(int i = 0; i < s[firstArrivalTime]; i++)
                {
                    order.add(p[firstArrivalTime]);
                }
                firstArrivalTime--;
            }

            //check if order is empty, if so, then add the next process with the shortest service time, service times many times
            if(order.isEmpty())
            {
                int shortestProcessTime = Integer.MAX_VALUE;
                String shortestProcessName = "";
                Set< HashMap.Entry<String, Integer> > st = arrivingProcessAndServiceTime.entrySet(); 

                //get the shortest next process
                for(HashMap.Entry<String, Integer> me:st)
                {
                    String tempStr = me.getKey();
                    int tempVal = arrivingProcessAndServiceTime.get(tempStr);
                    if(shortestProcessTime > tempVal)
                    {
                        shortestProcessTime = tempVal;
                        shortestProcessName = tempStr;
                    }
                }
                
                //add the shortest next process to the queue, service times many times
                for(int i = 0; i < shortestProcessTime; i++)
                {
                    order.add(shortestProcessName);
                }

                //remove the shortest process from the hashmap
                arrivingProcessAndServiceTime.remove(shortestProcessName);

            }
            //print next job
            printGraph(order);
            //dequeue it
            order.remove();
            //increment time
            time++;
            //loop through the arrival time array to check if any processes arrived, if so AND the service time it has is > 0, add it to hashtable
            for(int arrivalIndex = 0; arrivalIndex < a.length; arrivalIndex++)
            {
                if(a[arrivalIndex] == time && s[arrivalIndex] > 0)
                {
                    //add the service time of the arriving process to arrivingProcessServiceTime
                    arrivingProcessAndServiceTime.put(p[arrivalIndex], s[arrivalIndex]);
                }
            }
        }

        //just a line to make things look nice :)
        System.out.println("--------------------------------------------------------\n");
    }

    




    /**
    *  shortest remaining time, note: mine has D printing first. can change to b printing first by changing the helper function used in here by,
    *  changing this    >>>>>>      if(shortestTime >= tempVal && tempVal > 0)
    *  to this          >>>>>>      if(shortestTime > tempVal && tempVal > 0)
    *  k, nvm this doesnt work. ask for help? how to loop through hashmap back to front?
    */
    private static void srt(String[] p, int[] a, int[] s)
    {
        //queue to store the order of the printing, ex: {A, A, A, B, B, B, B, B, C, C...}
        Queue<String> order = new LinkedList<>();
        //hashmap to keep track of the arriving processes: <name, sevice_time> while the current process is running
        HashMap<String, Integer> arrivingProcessAndServiceTime = new HashMap<String, Integer>();

        //print processes row on the top. note: my output is vertical
        System.out.println("STR");
        System.out.println("");
        for(int i = 0; i < p.length; i++)
            System.out.print(p[i] + "\t");
        System.out.print("\n");

        //perform STR algorithm:
        //get total_service_time, which is the total time for all services + the arrival time of the first arrival
        int total_service_time = 0;
        for(int i = 0; i < p.length; i++)
        {
            total_service_time += s[i];
        }

        //timer to keep track of the current time of the processor
        int time = 0;
        //loop "total_service_time" amount of times
        while(time < total_service_time)
        {
            //add the first value to the queue and hashmap
            if(time == 0)    
            {
                order.add(p[0]);
                arrivingProcessAndServiceTime.put(p[0], s[0]);
            }

            //print graph
            printGraph(order);
            //dequeue, then decrement service time of dequeued process
            String removedProcess = order.remove();
            //System.out.println(arrivingProcessAndServiceTime.get(removedProcess));
            arrivingProcessAndServiceTime.replace(removedProcess, arrivingProcessAndServiceTime.get(removedProcess) - 1);

            //increment time
            time++;
            //loop through the arrival time array to check if any processes arrived, if so, and the service time it has is > 0, add it to hashtable
            for(int arrivalIndex = 0; arrivalIndex < a.length; arrivalIndex++)
            {
                if(a[arrivalIndex] == time && s[arrivalIndex] > 0)
                {
                    //add the service time of the arriving process to arrivingProcessServiceTime
                    arrivingProcessAndServiceTime.put(p[arrivalIndex], s[arrivalIndex]);
                }
            }

            //compare all service times of of arrived processes in the hashmap, then enqueue the lowest process
            enqueueShortestTimeRemaning(removedProcess, order, arrivingProcessAndServiceTime);
        }

        //just a line to make things look nice :)
        System.out.println("--------------------------------------------------------\n");
    }





    /**
    *  highest response ratio next
    *  formula for ratio: ratio = (waiting_time - service_time)/service_time
    *  waiting_time is (current_time - arrival time)
    *  because HRRN is non-preemptive, dont worry about a program running, then interrupted, then running again. <<< not a possible scenario
    */
    private static void hrrn(String[] p, int[] a, int[] s)
    {
        //queue to store the order of the printing, ex: {A, A, A, B, B, B, B, B, C, C...}
        Queue<String> order = new LinkedList<>();
        //hashmap to keep track of the arriving processes: <name, sevice_time> while the current process is running
        HashMap<String, Integer> arrivingProcessAndServiceTime = new HashMap<String, Integer>();
        //hashmap to keep track of the arriving processes: <name, arrival_time> while the current process is running
        HashMap<String, Integer> arrivingProcessAndArrivalTime = new HashMap<String, Integer>();

        //print processes row on the top. note: my output is vertical
        System.out.println("HRRN");
        System.out.println("");
        for(int i = 0; i < p.length; i++)
            System.out.print(p[i] + "\t");
        System.out.print("\n");

        //perform HRRN algorithm:
        //get total_service_time, which is the total time for all services
        int total_service_time = 0;
        for(int i = 0; i < p.length; i++)
        {
            total_service_time += s[i];
        }

        //timer to keep track of the current time of the processor
        int time = 0;
        //loop "total_service_time" amount of times
        while(time < total_service_time)
        {
            //add first process to queue
            if(time == 0)
            {
                //put first process into hashmap
                arrivingProcessAndArrivalTime.put(p[0], a[0]);
                arrivingProcessAndServiceTime.put(p[0], a[0]);
                //loop service_time # of times tp add the first process
                for(int i = 0; i < s[0]; i++)
                {
                    order.add(p[0]);
                }
            }
            //System.out.println("debug, size of arri:" + arrivingProcessAndArrivalTime.size());
            //System.out.println("debug, size of serv:" + arrivingProcessAndServiceTime.size());
            
            //check if order is empty, if so enqueue next process fully
            if(order.isEmpty())
            {
                double highestResponseRatioNext = 0.0;
                String hrrnName = "";
                Set< HashMap.Entry<String, Integer> > st = arrivingProcessAndServiceTime.entrySet();

                //get hrrn process
                for(HashMap.Entry<String, Integer> me:st)
                {
                    String tempStr = me.getKey();
                    int tempService = arrivingProcessAndServiceTime.get(tempStr);
                    int tempArrival = arrivingProcessAndArrivalTime.get(tempStr);
                    //System.out.println("time: "+ time + ", process: " + tempStr + ", waiting time: " + (time-tempArrival) + ", service time: " + tempService + ", ratio:" + ((double)(time-tempArrival)/tempService));
                    if(highestResponseRatioNext < ((double)(time - tempArrival)/ tempService))
                    {
                        highestResponseRatioNext = ((double)(time - tempArrival)/ tempService);
                        hrrnName = tempStr;
                    }
                }
                //System.out.println("time: "+ time + ", ratio:" + highestResponseRatioNext);
                
                //add hrrn process to the queue, service times many times
                for(int i = 0; i < arrivingProcessAndServiceTime.get(hrrnName); i++)
                {
                    order.add(hrrnName);
                }

                //System.out.println("debug:" + hrrnName);
                //remove the shortest process from the hashmap
                arrivingProcessAndServiceTime.remove(hrrnName);
            }
            

            //print graph
            printGraph(order);
            //dequeue
            order.remove();

            //loop amount_of_processes many times and remove all finished processes from hashmap
            for(int i = 0; i < p.length; i++)
            {
                if(arrivingProcessAndServiceTime.containsKey(p[i]))
                {
                    if(arrivingProcessAndServiceTime.get(p[i]) == 0)
                    {
                        arrivingProcessAndServiceTime.remove(p[i]);
                        arrivingProcessAndArrivalTime.remove(p[i]);
                    }
                }
            }

            //increment time
            time++;
            //loop through the arrival time array to check if any processes arrived, if so, and the service time it has is > 0, add it to hashtable
            for(int arrivalIndex = 0; arrivalIndex < a.length; arrivalIndex++)
            {
                if(a[arrivalIndex] == time && s[arrivalIndex] > 0)
                {
                    //add the service_time and arrival_time of the arriving process to arrivingProcessServiceTime
                    arrivingProcessAndServiceTime.put(p[arrivalIndex], s[arrivalIndex]);
                    arrivingProcessAndArrivalTime.put(p[arrivalIndex], a[arrivalIndex]);
                }
            }
        }

        //just a line to make things look nice :)
        System.out.println("--------------------------------------------------------\n");
    }

    /**
    *  feedback
    */
    private static void fb(String[] p, int[] a, int[] s)
    {
        //queue to store the order of the printing, ex: {A, A, A, B, B, B, B, B, C, C...}
        Queue<String> high = new LinkedList<>();
        Queue<String> med = new LinkedList<>();
        Queue<String> low = new LinkedList<>();
        //hashmap to keep track of the arriving processes: <name, sevice_time> while the current process is running
        HashMap<String, Integer> arrivingProcessAndServiceTime = new HashMap<String, Integer>();
        HashMap<String, String> arrivingProcessAndPriority = new HashMap<String, String>();

        //print processes row on the top. note: my output is vertical
        System.out.println("FB");
        System.out.println("");
        for(int i = 0; i < p.length; i++)
            System.out.print(p[i] + "\t");
        System.out.print("\n");

        //perform FB algorithm:
        //get total_service_time, which is the total time for all services
        int total_service_time = 0;
        for(int i = 0; i < p.length; i++)
        {
            total_service_time += s[i];
        }

        //the second arrival time
        int secondArrivalTime = 0;
        if(a.length > 1)
        {
            secondArrivalTime = a[1];
        }
        
        //timer to keep track of the current time of the processor
        int time = 0;
        //loop "total_service_time" amount of times
        while(time < total_service_time)
        {
            //initial run
            if(time == 0)    
            {
                arrivingProcessAndServiceTime.put(p[0], s[0]);
                arrivingProcessAndPriority.put(p[0], "high");
                high.add(p[0]);
                //loop second_arrival_time many times, while printing the first process
                while(time < secondArrivalTime)
                {
                    printGraph(high);
                    arrivingProcessAndServiceTime.replace(p[0], arrivingProcessAndServiceTime.get(p[0]) - 1);
                    time++;
                }

                //proceed with normal fb after the second process arrives
                high.add(p[1]);
                arrivingProcessAndServiceTime.put(p[1], s[1]);
                arrivingProcessAndPriority.put(p[1], "high");

                if(arrivingProcessAndServiceTime.get(p[0]) > 0)
                {
                    //remove a from high queue
                    high.remove();
                    //add a to med queue
                    med.add(p[0]);
                    arrivingProcessAndServiceTime.put(p[0], s[0] - time);
                    arrivingProcessAndPriority.put(p[0], "med");

                    //System.out.println("debug, size of high:" + high.size());
                    //remove from hashmap if service time is == 0
                    if(arrivingProcessAndServiceTime.get(p[0]) == 0)
                    {
                        arrivingProcessAndServiceTime.remove(p[0]);
                        arrivingProcessAndPriority.remove(p[0]);
                        high.remove();
                    }
                }
            }
            //System.out.println("debug, size of map:" + arrivingProcessAndPriority.size());
            //System.out.println("debug, size of high:" + high.size());
            //System.out.println("debug, size of med:" + med.size());
            //System.out.println("debug, size of map:" + arrivingProcessAndPriority.size());

            //begin work here:
            String printedFrom = "";
            //print graph, check if each queue is empty in order of priority
            if(!high.isEmpty())
            {
                printGraph(high);
                printedFrom = "high";
            }
            else if(!med.isEmpty())
            {
                printGraph(med);
                printedFrom = "med";
            }
            else 
            {
                printGraph(low);
                printedFrom = "low";
            }

            //dequeue, then decrement service time of dequeued process
            //if the service time > 0, then enqueue into lower priority queue, and update priority in hashmap
            String removedProcess = "";
            if(printedFrom == "high")
            {
                removedProcess = high.remove();
                if(arrivingProcessAndServiceTime.containsKey(removedProcess))
                {
                    arrivingProcessAndServiceTime.replace(removedProcess, arrivingProcessAndServiceTime.get(removedProcess) - 1);
                    if(arrivingProcessAndServiceTime.get(removedProcess) > 0)
                    {
                        med.add(removedProcess);
                        arrivingProcessAndPriority.replace(removedProcess, "med");
                    }
                }
            }
            else if(printedFrom == "med")
            {
                removedProcess = med.remove();
                if(arrivingProcessAndServiceTime.containsKey(removedProcess))
                {
                    arrivingProcessAndServiceTime.replace(removedProcess, arrivingProcessAndServiceTime.get(removedProcess) - 1);
                    if(arrivingProcessAndServiceTime.get(removedProcess) > 0)
                    {
                        low.add(removedProcess);
                        arrivingProcessAndPriority.replace(removedProcess, "low");
                    }
                }
            }
            else if(printedFrom == "low")
            {
                removedProcess = low.remove();
                if(arrivingProcessAndServiceTime.containsKey(removedProcess))
                {
                    arrivingProcessAndServiceTime.replace(removedProcess, arrivingProcessAndServiceTime.get(removedProcess) - 1);
                    if(arrivingProcessAndServiceTime.get(removedProcess) > 0)
                    {
                        low.add(removedProcess);
                    }
                }
            }
            /*
            System.out.println("debug, size of high:" + high.size());
            System.out.println("debug, size of med:" + med.size());
            System.out.println("debug, current time:" + time);
            */
            //loop amount_of_processes many times and remove all finished processes from hashmap
            for(int i = 0; i < p.length; i++)
            {
                if(arrivingProcessAndServiceTime.containsKey(p[i]))
                {
                    if(arrivingProcessAndServiceTime.get(p[i]) == 0)
                    {
                        arrivingProcessAndServiceTime.remove(p[i]);
                        arrivingProcessAndPriority.remove(p[i]);
                    }
                }
            }
            

            //increment time
            
            time++;
            //loop through the arrival time array to check if any processes arrived, if so, and the service time it has is > 0, add it to hashtable
            for(int arrivalIndex = 0; arrivalIndex < a.length; arrivalIndex++)
            {
                if(a[arrivalIndex] == time && s[arrivalIndex] > 0)
                {
                    //add the service time of the arriving process to arrivingProcessServiceTime
                    arrivingProcessAndServiceTime.put(p[arrivalIndex], s[arrivalIndex]);
                    arrivingProcessAndPriority.put(p[arrivalIndex], "high");
                    high.add(p[arrivalIndex]);
                }
            }

        }

        //just a line to make things look nice :)
        System.out.println("--------------------------------------------------------\n");
    }





    /**
    *  -----------------------------------------------------------------------------------------------------------------------------------------------------
    *  helper functions-------------------------------------------------------------------------------------------------------------------------------------
    *  -----------------------------------------------------------------------------------------------------------------------------------------------------
    */ 

    /*read file function and assign values to the arrays
    public static void readfile(File f, Scanner scan, String[] p, int[] a, int[] s)
    {
        for(int row = 0; row < NUM_PROCESSES; row++)
        {
            p[row] = scan.next();
            a[row] = scan.nextInt();
            s[row] = scan.nextInt();
            //System.out.print(process[row] + " ");
            //System.out.print(arrival_times[row] + " ");
            //System.out.print(service_times[row] + "\n");
        }
    }
    */

    //find the index of the smallest number in array
    public static int smallestNumbersIndex(int[] arr)
    {
        int index = 0;
        int min = arr[index];

        for (int i = 1; i < arr.length; i++) 
        {
            if (arr[i] < min) 
            {
                min = arr[i];
                index = i;
            }
        }
        return index;
    }

    //function to return the min value in the array
    public static int minValue(int[] arr)
    {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < arr.length; i++)
        {
            if(min > arr[i])
                min = arr[i];
        }
        return min;
    }

    //function to decrement the service time of the removed job. if the service time after decrementing is > 0, then add it back into the queue
    public static void decrementServiceTimeofRemovedJob(String str, Queue<String> q, String[] p, int[] s)
    {
        //loop through the process array
        for(int i = 0; i < p.length; i++)
        {
            if(str == p[i])
            {
                s[i]--;
                if(s[i] > 0)
                    q.add(p[i]);
            }
        }
    }

    //function to compare all service times of of arrived processes in the hashmap, then enqueue the shortest remaning time process
    public static void enqueueShortestTimeRemaning(String removedProcess, Queue<String> q, HashMap<String, Integer> map)
    {
        int shortestTime = Integer.MAX_VALUE;
        String shortestTimeProcess = "";
        if(map.get(removedProcess) > 0)
        {
            shortestTime = map.get(removedProcess);
            shortestTimeProcess = removedProcess;
        }
        
        
        Set< HashMap.Entry<String, Integer> > st = map.entrySet(); 
        /* trying to make it work :<
        List< HashMap.Entry<String, Integer> > list = new ArrayList<>();
        list.addAll(st);

        int count = list.size();
        while(count > 0)
        {
            String tempStr = map.;
            int tempVal = map.get(tempStr);
            if(shortestTime >= tempVal && tempVal > 0)
            {
                shortestTime = tempVal;
                shortestTimeProcess = tempStr;
            }
            count--;
        }
        */
        //loop through the hashmap, working code
        for(HashMap.Entry<String, Integer> me:st)
        {
            String tempStr = me.getKey();
            int tempVal = map.get(tempStr);
            //System.out.println("debug: "+tempStr+" "+tempVal+" "+shortestTime);
            if(shortestTime > tempVal && tempVal > 0)
            {
                shortestTime = tempVal;
                shortestTimeProcess = tempStr;
            }
        }

        //enqueue the shortest time remaining process
        q.add(shortestTimeProcess);
    }

    // takes a queue as parameter, print the correct amount of "\t" and "X", then dequeue
    public static void printGraph(Queue<String> q)
    {
        //peek at the job at the top
        String job = q.peek();
        //print the correct amount of "\t" and an "X".
        if(job.equals("A"))
            System.out.println("X");
        else if(job.equals("B"))
            System.out.println("\tX");
        else if(job.equals("C"))
            System.out.println("\t\tX");
        else if(job.equals("D"))
            System.out.println("\t\t\tX");
        else if(job.equals("E"))
            System.out.println("\t\t\t\tX");
        else if(job.equals("F"))
            System.out.println("\t\t\t\t\tX");
        else if(job.equals("G"))
            System.out.println("\t\t\t\t\t\tX");
        else if(job.equals("H"))
            System.out.println("\t\t\t\t\t\t\tX");
        else if(job.equals("I"))
            System.out.println("\t\t\t\t\t\t\t\tX");
        else if(job.equals("J"))
            System.out.println("\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("K"))
            System.out.println("\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("L"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("M"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("N"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("O"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("P"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("Q"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("R"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("S"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("T"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("U"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("V"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("W"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("X"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("Y"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        else if(job.equals("Z"))
            System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tX");
        
        //dequeue, better to do this step seperately, need this way for round robin
        //q.remove();
    }
}//end project3 class