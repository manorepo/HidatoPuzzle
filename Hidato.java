import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.*;
 
public class Hidato extends JFrame {
    
  ArrayList<Integer> inputFieldsPos=new ArrayList<Integer>();
  int gameSize=4;
     
    public Hidato(String name) {
        super(name);
       // setResizable(false);
    }
     
    
     
    public void addComponentsToPane(final Container pane) {
     
        final JPanel gridPanel = new JPanel();
        final JPanel buttonPanel=new JPanel();
       // compsToExperiment.setLayout(experimentLayout);
        ArrayList<JTextField> gridFields=new ArrayList<JTextField>();
        gridPanel.setLayout(new GridLayout(gameSize,gameSize));
         
        //Set up components preferred size
        JButton b = new JButton("Just fake button");
        Dimension buttonSize = b.getPreferredSize();
       
         
         createGrid(gridPanel,gridFields);
      
      
        //Button Panel
        JButton solveButton=new JButton("Solve");
        solveButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//gridFields.get(0).setText("hello");
				try {
					FileOutputStream inputFile=new FileOutputStream("input.txt");
					int count=0;
					String size="#const n = "+gameSize+".\n";
	        		byte[] b=size.getBytes();
	        		try {
						inputFile.write(b);
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	        		boolean bvalid=false;
					for(JTextField field:gridFields){
						
						++count;
			        	if(!field.getText().equals("")){
			        		if(!isInteger(field.getText())){
			        			JOptionPane.showMessageDialog(gridPanel,
									    "Not valid input - '"+field.getText()+"'",
									    "Hidato Warning",
									    JOptionPane.WARNING_MESSAGE);
								
			        			inputFile.close();
			        			bvalid=false;
			        			break;
			        		}
							bvalid=true;
			        		inputFieldsPos.add(count-1);
			        		int xco;
			        		int yco;
			        		int modValue=count%gameSize;
			        		int queValue=count/gameSize;
			        		if(modValue==0){
			        			xco=queValue;
			        		}
			        		else{
			        			xco=queValue+1;
			        		}
			        		if(modValue==0)
			        		{
			        			yco=gameSize;
			        		}
			        		else{
			        			yco=modValue;
			        		}
			        		String text="initialpos("+xco+","+yco+","+field.getText()+")."+"\n";
			        		byte[] bytes=text.getBytes();
			        		try {
								inputFile.write(bytes);
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			        	}
			        }
					inputFile.close();
					if(bvalid){
					Process p = Runtime.getRuntime().exec("cmd /C clingo hidatologic.sm input.txt | mkatoms > output");
					BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String line;
					boolean  bOk=true;
					while((line = error.readLine()) != null){
					    System.out.println(line);
					    bOk=false;
					}
					error.close();
 
					p.waitFor();
					if(bOk)
						readOutput(gridFields);
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			public boolean isInteger(String s) {
			    try { 
			        Integer.parseInt(s); 
			    } catch(NumberFormatException e) { 
			        return false; 
			    } catch(NullPointerException e) {
			        return false;
			    }
			    // only got here if we didn't return false
			    return true;
			}
			private void readOutput(ArrayList<JTextField> gridFields) {
				// TODO Auto-generated method stub
				String fileName = "output";
				ArrayList<String> list = new ArrayList<>();

				try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

					//br returns as stream and convert it into a List
					list = (ArrayList<String>) br.lines().collect(Collectors.toList());

				} catch (IOException e) {
					e.printStackTrace();
				}
			
				//list.forEach(System.out::println);
				for(String value:list){
					if(value.contains("no models found"))
					{
						JOptionPane.showMessageDialog(gridPanel,
							    "No solution exist for given set.",
							    "Hidato Warning",
							    JOptionPane.WARNING_MESSAGE);
						break;
					}
					if(value.charAt(0)=='p'){
						char[] valueChar=value.toCharArray();
					
						int xco=Character.getNumericValue(valueChar[2]);
						int yco=Character.getNumericValue(valueChar[4]);
					//	int outputValue=Character.getNumericValue(valueChar[6]);
						String outputValue=value.substring(value.lastIndexOf(',')+1,value.lastIndexOf(')'));
						int pos=((xco-1)*gameSize)+yco-1;
						if(inputFieldsPos.contains(pos)==false)
						{
							gridFields.get(pos).setForeground(Color.RED);
						}
						gridFields.get(pos).setText(outputValue);
						gridFields.get(pos).setEditable(false);
					}
				}
			}
        	
        });
        
        JButton clearButton=new JButton("Clear");
        clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				for(JTextField field:gridFields){
					inputFieldsPos.clear();
					field.setForeground(Color.BLACK);
					field.setText("");
					field.setEditable(true);
				}
				
			}
        	
        });
        JButton closeButton=new JButton("Close");
        closeButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Hidato.super.dispose();
			}
        	
        });
        buttonPanel.add(clearButton);
        buttonPanel.add(solveButton);
        buttonPanel.add(closeButton);
        String[] sizes = new String[] {"3X3", "4X4","5X5","6X6","7X7"};

JComboBox<String> sizesCB = new JComboBox<>(sizes);
sizesCB.setSelectedItem("4X4");
sizesCB.addActionListener (new ActionListener () {
    public void actionPerformed(ActionEvent e) {
    	String selectedSize = (String) sizesCB.getSelectedItem();
    	if(selectedSize.equals("3X3"))
    	{
    		gameSize=3;
    	}
    	else if(selectedSize.equals("4X4"))
    	{
    		gameSize=4;
    	}
    	else if(selectedSize.equals("5X5"))
    	{
    		gameSize=5;
    	}
    	else if(selectedSize.equals("6X6"))
    	{
    		gameSize=6;
    	}
    	else if(selectedSize.equals("7X7"))
    	{
    		gameSize=7;
    	}
    	
    	//gameSize=Integer.parseInt(selectedSize);
    	createGrid(gridPanel,gridFields);
    }
});

//add to the parent container (e.g. a JFrame):


//get the selected item:
//
pane.add(sizesCB, BorderLayout.NORTH);
        pane.add(gridPanel, BorderLayout.CENTER);
       // pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(buttonPanel, BorderLayout.SOUTH);
    }
     
    private void createGrid(JPanel gridPanel,ArrayList<JTextField> gridFields) {
    	gridPanel.removeAll();
    	gridFields.clear();
    	gridPanel.setLayout(new GridLayout(gameSize,gameSize));
    	//gridPanel.repaint();
    	int height=60;
        int width=60;
        Font gridFont = new Font("SansSerif", Font.BOLD, 20);
         for(int i=0;i<(gameSize*gameSize);i++){
      	   JTextField field=new JTextField();
      	   field.setName("field"+(i+1));
      	   field.setPreferredSize(new Dimension(height,width));
      	   field.setFont(gridFont);
      	   field.setHorizontalAlignment(JTextField.CENTER);
      	   gridFields.add(field);
         }
          for(JTextField field:gridFields){
          	 gridPanel.add(field);
          }
          gridPanel.revalidate();
          gridPanel.repaint();
	}



	/**
     * Create the GUI and show it.  For thread safety,
     * this method is invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
    	Hidato frame = new Hidato("Hidato Puzzle");
    	frame.setSize(500,480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
      
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}