 // Example of a Simulation. This test runs the nodes on a random graph.
// At the end, it will print out the Transaction ids which each node
// believes consensus has been reached upon. You can use this simulation to
// test your nodes. You will want to try creating some deviant nodes and
// mixing them in the network to fully test.

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;

public class Simulation {

   public static void main(String[] args) {

	  // 命令行有四个参数      .1 .15 .01 10
      // p_graph (.1, .2, .3),
      // p_malicious (.15, .30, .45), 
	  // p_txDistribution (.01, .05, .10), 
      // numRounds (10, 20). 
	  // 你应该尝试测试所有的3x3x3x2 = 54中组合.
	   
      int numNodes = 100;
      double p_graph = Double.parseDouble(args[0]); // 随机图的参数parameter for random graph: prob. that an edge will exist
      double p_malicious = Double.parseDouble(args[1]); //设置为恶意节点的可能性 prob. that a node will be set to be malicious
      double p_txDistribution = Double.parseDouble(args[2]); // 指定初始交易的可能性probability of assigning an initial transaction to each node 
      int numRounds = Integer.parseInt(args[3]); // 模拟的轮数number of simulation rounds your nodes will run for

      // pick which nodes are malicious and which are compliant
      Node[] nodes = new Node[numNodes];
      for (int i = 0; i < numNodes; i++) {
         if(Math.random() < p_malicious)
            // When you are ready to try testing with malicious nodes, replace the
            // instantiation below with an instantiation of a MaliciousNode
            nodes[i] = new MaliciousNode(p_graph, p_malicious, p_txDistribution, numRounds);
         else
            nodes[i] = new CompliantNode(p_graph, p_malicious, p_txDistribution, numRounds);
      }


      // initialize random follow graph
      boolean[][] followees = new boolean[numNodes][numNodes]; // followees[i][j] is true iff i follows j
      for (int i = 0; i < numNodes; i++) {
         for (int j = 0; j < numNodes; j++) {
            if (i == j) continue;
            if(Math.random() < p_graph) { // p_graph is .1, .2, or .3
               followees[i][j] = true;
            }
         }
      }

      // notify all nodes of their followees
      // 通知节点其跟随的节点
      for (int i = 0; i < numNodes; i++)
         nodes[i].setFollowees(followees[i]);

      // initialize a set of 500 valid Transactions with random ids
      // validTxIds保存所有的初始交易
      int numTx = 500;
      HashSet<Integer> validTxIds = new HashSet<Integer>();
      Random random = new Random();
      for (int i = 0; i < numTx; i++) {
         int r = random.nextInt();
         validTxIds.add(r);
      }


      // distribute the 500 Transactions throughout the nodes, to initialize
      // the starting state of Transactions each node has heard. The distribution
      // is random with probability p_txDistribution for each Transaction-Node pair.
      
      // 随机为节点分配初始交易
      for (int i = 0; i < numNodes; i++) {
         HashSet<Transaction> pendingTransactions = new HashSet<Transaction>();
         for(Integer txID : validTxIds) {
            if (Math.random() < p_txDistribution) // p_txDistribution is .01, .05, or .10.
               pendingTransactions.add(new Transaction(txID));
         }
         nodes[i].setPendingTransaction(pendingTransactions);
      }


      // Simulate for numRounds times
      //模拟多轮
      for (int round = 0; round < numRounds; round++) { // numRounds is either 10 or 20

         // gather all the proposals into a map. The key is the index of the node receiving
         // proposals. The value is an ArrayList containing 1x2 Integer arrays. The first
         // element of each array is the id of the transaction being proposed and the second
         // element is the index # of the node proposing the transaction.
    	  
    	  //allProposals保存每个节点收到的所有交易
         HashMap<Integer, Set<Candidate>> allProposals = new HashMap<>();

         for (int i = 0; i < numNodes; i++) { //遍历所有节点
            Set<Transaction> proposals = nodes[i].sendToFollowers();//调用节点的sendToFollowers()
            for (Transaction tx : proposals) { //遍历返回的交易
               if (!validTxIds.contains(tx.id)) //如果不是初始交易则跳过
                  continue; // ensure that each tx is actually valid
               
               //tx肯定是初始交易中....
               for (int j = 0; j < numNodes; j++) {//遍历所有节点，如果j跟随i
                  if(!followees[j][i]) continue; // tx only matters if j follows i

                  //j跟随i，为j创建一个， Set<Candidate> ，保存i节点收到的，最后放在allProposals中
                  if (!allProposals.containsKey(j)) {
                	  Set<Candidate> candidates = new HashSet<>();
                	  allProposals.put(j, candidates);
                  }
                  
                  Candidate candidate = new Candidate(tx, i);//tx交易，发送者是i
                  allProposals.get(j).add(candidate);
               }

            }
         }

         // Distribute the Proposals to their intended recipients as Candidates
         for (int i = 0; i < numNodes; i++) {
            if (allProposals.containsKey(i))
               nodes[i].receiveFromFollowees(allProposals.get(i));
         }
      }

      // print results
      for (int i = 0; i < numNodes; i++) {
         Set<Transaction> transactions = nodes[i].sendToFollowers();
         System.out.println("Transaction ids that Node " + i + " believes consensus on:");
         for (Transaction tx : transactions)
            System.out.println(tx.id);
         System.out.println();
         System.out.println();
      }

   }


}

