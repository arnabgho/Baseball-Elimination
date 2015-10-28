/**
 * Auto Generated Java Class.
 */
import java.util.*;
import java.io.*;
public class BaseballElimination {
    
    int numTeams;
    Bag<String> teamString;
    int[][] games;
    int[] win;
    int[] loss;
    int[] rem;
    int inf;
    int sum;
    HashMap <String,Integer> map;
    int[][] gameVertices;
    int[] teamVertices;
    HashMap <Integer,String> allTeam;
    boolean possible;
    public BaseballElimination(String filename){
        inf=1000000000;
        allTeam=new HashMap<Integer,String>();
        map=new HashMap<String,Integer>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            teamString=new Bag<String>();
            int i=0;
            int first=1;
            try{
                while ((line=br.readLine())!=null) {
                    
                    // process the line.
                    if(first==1){
                        first=0;
                        numTeams=Integer.parseInt(line);
                        win=new int[numTeams];
                        loss=new int[numTeams];
                        rem=new int[numTeams];
                        games=new int[numTeams][numTeams];
                        gameVertices=new int[numTeams][numTeams];
                        teamVertices=new int[numTeams];
                    }    
                    else{
                        System.out.println("line "+line+"\n");
                        String[] splited = line.split("\\s+");
                        allTeam.put(i,splited[0]);
                        teamString.add(splited[0]);
                        map.put(splited[0],i);
                        win[i]=Integer.parseInt(splited[1]);
                        loss[i]=Integer.parseInt(splited[2]);
                        rem[i]=Integer.parseInt(splited[3]);
                        for(int j=0;j<numTeams;j++){
                            games[i][j]=Integer.parseInt(splited[j+4]);
                        }
                        i++;
                    }    
                }  
            }
            catch(Exception e){
                System.out.println("Hi "+e.getMessage());
            }    
            try{
                br.close();
            }
            catch(Exception e){
                System.out.println("Hi2 " +e.getMessage());
            }    
        }
        catch(FileNotFoundException fnfe) { 
            System.out.println(fnfe.getMessage());
        }   
    }
    
    public int numberOfTeams(){
        return numTeams;
    }
    public Iterable<String> teams(){
        return teamString;
    }
    
    public int wins(String team){
        if(!map.containsKey(team))
            System.out.println("The team "+team+ " doesn't exist");
        
        int pos=map.get(team);
        return win[pos];
    }
    
    public int losses(String team){
        if(!map.containsKey(team))
            System.out.println("The team "+team+ " doesn't exist");
        
        int pos=map.get(team);
        return loss[pos];
    }
    public int remaining(String team){
        if(!map.containsKey(team))
            System.out.println("The team "+team +" doesn't exist");
        
        int pos=map.get(team);
        return rem[pos];
    }
    public int against(String team1,String team2){
        if(!map.containsKey(team1))
            System.out.println("The team1 "+team1 + " doesn't exist");
        
        if(!map.containsKey(team2))
            System.out.println("The team2"+team2 +" doesn't exist");
       
        int pos1=map.get(team1);
        int pos2=map.get(team2);
        return games[pos1][pos2];
    }
       
    
    private FordFulkerson returnMaxFlow(String team){
//        System.out.println("Team "+team);
        int teamID=map.get(team);
//        System.out.println("TeamID "+teamID);
        int s=0;
        int pos=1;
        int n=numTeams,i,j;
        //Mapping of game vertices has to be correct
        for(i=0;i<n;i++){
            for(j=i+1;j<n;j++){
                gameVertices[i][j]=pos;
                gameVertices[j][i]=pos;
                pos++;
            }
        }
        //Mapping of team vertices has to be correct
        for(i=0;i<n;i++){
            teamVertices[i]=pos;
            pos++;
        }
        //Destination
        int t=pos;
        int numV=((n*(n+1))/2)+2;
        
//        System.out.println(" t "+t);
//        System.out.println("numV "+numV);
        FlowNetwork G=new FlowNetwork(numV);
        //Add All edges related to the game vertices
        sum=0;
        for(i=0;i<n;i++){
            for(j=i+1;j<n;j++){
               sum+=games[i][j]; 
               pos=gameVertices[i][j];
               FlowEdge e=new FlowEdge(s,pos,games[i][j]);
               FlowEdge e1=new FlowEdge(pos,teamVertices[i],inf);
               FlowEdge e2=new FlowEdge(pos,teamVertices[j],inf);
               G.addEdge(e);G.addEdge(e1);G.addEdge(e2);
            }
        }
        
        int tot=win[teamID]+rem[teamID];
        for(i=0;i<n;i++){
            if(tot>win[i]){
                FlowEdge e=new FlowEdge(teamVertices[i],t,tot-win[i]);
                G.addEdge(e);
            }    
            else{
                possible=false;
            }    
            
        }
        FordFulkerson maxflow=new FordFulkerson(G,s,t);
        return maxflow;
    }
    
    public boolean isEliminated(String team){
        FordFulkerson maxflow;
        possible=true;
        maxflow=returnMaxFlow(team);
//        System.out.println("maxflow "+maxflow.value());
//        System.out.println("Sum "+sum);
        if(!possible)
            return true;
        else if((int)maxflow.value()==sum)
            return false;
        else
            return true;
    } 
    public Iterable<String> certificateOfElimination(String team){
        if(!isEliminated(team))
            return null;
        else{
            FordFulkerson maxflow=returnMaxFlow(team);
            Bag<String> subset=new Bag<String>();
            int teamID=map.get(team);
            for(int i=0;i<numTeams;i++){
                if(i!=teamID){
                    if(maxflow.inCut(teamVertices[i])){
                        String temp=allTeam.get(i);
                        subset.add(temp);
                    }    
                }
            }
            return subset;
        }
    }
//    public static void main(String args[]) throws IOException{
//        BaseballElimination b=new BaseballElimination("teams4.txt");
//    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
    
}
