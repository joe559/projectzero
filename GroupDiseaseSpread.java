import java.util.Scanner;
import java.io.*;

//Michael Michel
//Julia A.
//Jose Nunez
//Vanisa S.
public class GroupDiseaseSpread
{
    
    private static int numIndividuals;
    private static int numTimeSteps;
    private static double infectionRate = -1;
    private static double recoverRate = -1;
    private static int squareRow;
    private static FileOutputStream files = null;
    private static PrintWriter outFS = null;
    private static FileInputStream fileString = null;
    private static Scanner inFS = null;
    private static String stepBack;
    private static String currentStep;
    private static String Positions = "MathPos.txt";
    private static int TimeCounter = 0;
    private static int patientZeroRow = -1;
    private static int patientZeroCol = -1;

    public GroupDiseaseSpread(int population, int totalTime, double iRate, double rRate)
    {
        numIndividuals = population;
        squareRow = (int)Math.sqrt(population);
        numTimeSteps = totalTime;
        infectionRate = iRate;
        recoverRate = rRate;
    }
    
    public GroupDiseaseSpread()
    {
        numIndividuals = 25;
        squareRow = 5;
        numTimeSteps = 5;
        infectionRate = 0.6;
        recoverRate = 0.4;
    }
    
    public static void main(String[] args) throws IOException
    {
        Scanner scan = new Scanner(System.in);
        boolean isPerfectSquare;
        do{
            System.out.println("Enter the size of the population (must be perfect square)");
            numIndividuals = scan.nextInt();
            squareRow = (int) (Math.sqrt(numIndividuals));
            isPerfectSquare = ((squareRow * squareRow) == numIndividuals);
        } while(isPerfectSquare == false || numIndividuals <=0);
        
        while(infectionRate < 0 || infectionRate >= .25) {
            System.out.println("Enter the infection rate (must be between 0 and .25, inclusive)");
            infectionRate = scan.nextDouble();
        }
        while (recoverRate < 0 || recoverRate > 1) {
            System.out.println("Enter the recover rate (must be between 0 and 1, inclusive)");
            recoverRate = scan.nextDouble();
        }
        while (numTimeSteps < 1) {
            System.out.println("Enter the number of time steps (default is 1");
            numTimeSteps = scan.nextInt();
        }
        
        
        while(patientZeroRow > squareRow - 1 || patientZeroRow < 0)
        {
            System.out.println("Enter the row of the Patient Zero:");
            patientZeroRow = scan.nextInt();
        }
        while(patientZeroCol > squareRow - 1 || patientZeroCol < 0)
        {
            System.out.println("Enter the column of the Patient Zero:");
            patientZeroCol = scan.nextInt();
        }
        scan.close();
        
        GridStatus0();
        
        for(int i = 0; i < numTimeSteps; i++)
        {
            TimeStepChange();
        }
    }
    
    //accessors
    public static int getPopulation()
    {return numIndividuals;}
    
    public static int getTimeStep()
    {return numTimeSteps;}
    
    public static double getIRate()
    {return infectionRate;}
    
    public static double getRRate()
    {return recoverRate;}
    
    public static void setLastStep(String fileName)
    {
        stepBack = fileName;
    }
    
    public String getLastStep()
    {
        return stepBack;
    }
    
    public static void setCurrentStep(String fileName)
    {
        currentStep = fileName;
    }
    
    public static void GridStatus0() throws IOException
    {
        files = new FileOutputStream("phase0.txt");
        outFS = new PrintWriter(files);
        setLastStep("phase0.txt");
        for(int i = 0; i < squareRow; i++)
        {
            for(int j = 0; j < squareRow; j++)
            {
                if(i == patientZeroRow && j == patientZeroCol)
                {
                System.out.print('I');
                outFS.print('I');
                }
                else
                {
                System.out.print('S');
                outFS.print('S');
                }
            }
            System.out.println();
            outFS.println();
        }
        System.out.println();
        outFS.close();
        GridPositions();
        
    }
    
    public static void GridPositions() throws IOException
    {
        files = new FileOutputStream(Positions);
        outFS = new PrintWriter(files);
        for(int i = 0; i < squareRow; i++)
        {
            for(int j = 0; j < squareRow; j++)
            {
                
                if ((i == 0 && j == 0) || (i + 1 == squareRow && j + 1 == squareRow) || (i == 0 && j + 1 == squareRow)
                        || (i + 1 == squareRow && j == 0)) {
                    outFS.print('C');
                } else if (i == 0 || j == 0 || i + 1 == squareRow || j + 1 == squareRow) {
                    outFS.print('B');
                } else {
                    outFS.print('N');
                }
            }
            outFS.println();
        }
        outFS.close();
    
    }
    
    public static char getPreviousStatus(int positionX, int positionY) throws IOException
    {
        fileString = new FileInputStream(stepBack);
        inFS = new Scanner(fileString);
        String pastLine;
        char status = 'a';
        for(int i = 0; i < squareRow; i++)
        {
            pastLine = inFS.nextLine();
              if(i == positionX)
                {
                    status = pastLine.charAt(positionY);
                }
            }
        fileString.close();
        return status;
    }
    
    //PosArith = Positional Arithmetics
    public static char getPosArith(int positionX, int positionY) throws IOException
    {
        fileString = new FileInputStream(Positions);
        inFS = new Scanner(fileString);
        String pastLine;
        char mathType = 'a';
        for(int i = 0; i < squareRow; i++)
        {
            pastLine = inFS.nextLine(); 
            if(i == positionX)
            {
                mathType = pastLine.charAt(positionY);
            }
        }
        fileString.close();
        return mathType;
    }
    
    public static void nameGen(int timeStep)
    {
        String fileName = "phase" + timeStep+".txt";
        setCurrentStep(fileName);
    }
    
    public static boolean calculateInfection(int positionX, int positionY) throws IOException
    {
        int infectedNeighborCount = 0;
        double infectionChance = getIRate();
        int neighbors = 0;
        
        if (getPosArith(positionX, positionY) == 'C') 
        {
            
            if (positionX == 0 && positionY == 0) //top left
            {
                if (getPreviousStatus(positionX + 1, positionY) == 'I') //below
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX, positionY + 1) == 'I') //right
                    infectedNeighborCount++;
            } 
            else if (positionX == squareRow - 1 && positionY == 0) //bottom left 
            {
                if (getPreviousStatus(positionX - 1, positionY) == 'I') //above
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX, positionY + 1) == 'I') //right
                    infectedNeighborCount++;
            } 
            else if (positionY == squareRow - 1 && positionY == squareRow - 1) //bottom right
            {
                if (getPreviousStatus(positionX - 1, positionY) == 'I') //above
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX, positionY - 1) == 'I') //left
                    infectedNeighborCount++;
            }
            else //top right
            {
                if (getPreviousStatus(positionX, positionY - 1) == 'I') //left
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX + 1, positionY) == 'I') //below
                    infectedNeighborCount++;
            }
        }
        
         if (getPosArith(positionX, positionY) == 'B')
         {
             
            if (positionX == 0) //left wall
            {
                if (getPreviousStatus(positionX + 1, positionY) == 'I') //below
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX, positionY + 1) == 'I') //right
                    infectedNeighborCount++;
                if(getPreviousStatus(positionX, positionY - 1) == 'I')  //left
                    infectedNeighborCount++;
            } 
            else if (positionY == 0) //top wall
            {
                if (getPreviousStatus(positionX + 1, positionY) == 'I') //below
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX, positionY + 1) == 'I') //right
                    infectedNeighborCount++;
                if(getPreviousStatus(positionX - 1, positionY) == 'I') //above
                    infectedNeighborCount++;
            }
            else if(positionX == squareRow - 1) //bottom wall
            {
                if (getPreviousStatus(positionX, positionY - 1) == 'I') //left
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX, positionY + 1) == 'I') //right
                    infectedNeighborCount++;
                if(getPreviousStatus(positionX - 1, positionY) == 'I') //above
                    infectedNeighborCount++;
            }
            else if(positionY == squareRow - 1) //right wall
            {
                if (getPreviousStatus(positionX + 1, positionY) == 'I') //below
                    infectedNeighborCount++;
                if (getPreviousStatus(positionX, positionY - 1) == 'I') //left
                    infectedNeighborCount++;
                if(getPreviousStatus(positionX - 1, positionY) == 'I') //above
                    infectedNeighborCount++;
            }
        } 
        
        if(getPosArith(positionX, positionY) == 'N')
        {
            
            if (getPreviousStatus(positionX + 1, positionY) == 'I') //below
                    infectedNeighborCount++;
            if (getPreviousStatus(positionX, positionY - 1) == 'I') //left
                    infectedNeighborCount++;
            if(getPreviousStatus(positionX - 1, positionY) == 'I') //above
                    infectedNeighborCount++;
            if (getPreviousStatus(positionX, positionY + 1) == 'I') //right
                    infectedNeighborCount++;
        }
        
        infectionChance = infectionChance * infectedNeighborCount;
        
        return ((Math.random() * 100) > (infectionChance * 100));
    }
    
    public static boolean calculateRecovery()
    {
        return ((Math.random() * 100 ) > (getRRate() * 100));
    }
    

    public static void Summarize() throws IOException
    {
        fileString = new FileInputStream(currentStep);
        inFS = new Scanner(fileString);
        String pastLine;
        char status = 'a';
        int countI = 0;
        int countS = 0;
        int countR = 0;
        for(int i = 0; i < squareRow + 1; i++)
        {
            pastLine = inFS.nextLine();
            for(int j = 0; j < squareRow; j++)
            {
                if(pastLine.charAt(j) == 'I')
                {countI++;}
                if(pastLine.charAt(j) == 'R')
                {countR++;}
                if(pastLine.charAt(j) == 'S')
                {countS++;}
            }
            
        }
        
        outFS.println("Number of Infected: " + countI);
        outFS.println("Number of Recovered: " + countR);
        outFS.println("Number of Potential Cases: " + countS);
        outFS.println("Current Ratio: Infected / Total Population " + countI + "/" + numIndividuals);
        fileString.close();
        
        
        System.out.println("Number of Infected: " + countI);
        System.out.println("Number of Recovered: " + countR);
        System.out.println("Number of Potential Cases: " + countS);
        System.out.println("Current Ratio: Infected / Total Population " + countI + "/" + numIndividuals);
        
    }
    
    public static void TimeStepChange() throws IOException
    {
        TimeCounter++;
        nameGen(TimeCounter);
        
        files = new FileOutputStream(currentStep);
        outFS = new PrintWriter(files);
        
        
        int x = 0;
        if(TimeCounter > 1)
        {x++;}
        for(int i = 0; i < squareRow; i++)
        {

            for(int j = 0; j < squareRow ; j++)
            {
               if(getPreviousStatus(i,j) == 'S')
                {
                    if(calculateInfection(i,j) == true)
                    {
                        outFS.print('S');
                        System.out.print('S');
                    }
                    else
                    {
                        outFS.print('I');
                        System.out.print('I');
                    }
                }
                if(getPreviousStatus(i,j) == 'I')
                {
                    if(calculateRecovery() == true)
                    {
                        outFS.print('I');
                        System.out.print('I');
                    }
                    else
                    {
                        outFS.print('R');
                        System.out.print('R');
                    }
                }
                if(getPreviousStatus(i,j) == 'R')
                    {
                        outFS.print('R');
                        System.out.print('R');
                    }
                
            }
            
            outFS.println();
            System.out.println();
        }
        outFS.println(currentStep);
        System.out.println(currentStep);
        
        outFS.close();
        Summarize();
        setLastStep(currentStep);
    }
}
