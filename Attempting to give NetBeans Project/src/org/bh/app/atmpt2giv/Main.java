package org.bh.app.atmpt2giv;

import bht.tools.fx.LookAndFeelChanger;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 * Attempts to replicate the behavior of a delightful little GIF. I know it's ugly and static but it's meant to be a joke, not
 * a sparkling example of OOP :P
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 *		- 2014-10-24 (1.0.0) - Kyli created Main
 * @since 2014-10-24
 */
public class Main
{
	public static final String APP_NAME = "Attempting to Give";
	public static final String MESSAGES[] = {"\r\nAttempting to give %s%e", "Unable to give %s. \r\nStopping%e", "Process failed. \r\n%T not given."};
	public static final String APP_VERSION = "1.0.0";
	public static final String DEF_THING_TO_GIVE = "crap";
	public static final String FAILURE = "Failed";
	
	public static final String[] ELLIPSIS_PROGRESSION = {".", "..", "..."};
	
	public static final char MSG_REPL_CHAR = '%';
	public static final char MSG_REPL_THING = 't';
	public static final char MSG_REPL_THING_UPPER = 'T';
	public static final char MSG_REPL_THING_SING = 's';
	public static final char MSG_REPL_THING_SING_UPPER = 'S';
	public static final char MSG_REPL_ELLIP = 'e';
	
    public static final Dimension MAX_WIN_BOUNDS;
	
	static
	{
		// we need to do this first because it runs on another thread, which might make the application look strange if some
		// components are added in the middle of changing.
		LookAndFeelChanger.setLookAndFeel(LookAndFeelChanger.SYSTEM);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		DisplayMode dm = ge.getScreenDevices()[0].getDisplayMode();
		MAX_WIN_BOUNDS = new Dimension(dm.getWidth(), dm.getHeight());
	}
	
	private static String thingToGive, thingSingular;
	private static Timer timer;
	private static JDialog dialog;
	private static JTextArea status;
	private static JProgressBar progressBar;
	private static JButton cancelButton;
	private static JPanel messagePanel;
	private static JOptionPane optionPane;
	
	private static int step = 0;
	
	/**
	 * The main launcher for Attempting to give
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		StringBuilder thingToGiveBuilder = new StringBuilder();
		if (args.length > 0)
		{
			for (String arg : args)
				thingToGiveBuilder.append(arg).append(' ');
			thingToGiveBuilder.deleteCharAt(thingToGiveBuilder.length() - 1);
		}
		else
			thingToGiveBuilder.append(DEF_THING_TO_GIVE);
		thingToGive = thingToGiveBuilder.toString();
		thingSingular = "a" + (isVowel(thingToGive.charAt(0)) ? 'n' : "") + ' ' + thingToGive;
		
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		cancelButton = new JButton("Cancel");
		
		{// set up GUI
			messagePanel = new JPanel();

			{ // init and layout components
				messagePanel.setLayout(new GridBagLayout());
				GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH,
						GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0);
				{ // init and add status label
					status = new JTextArea(message(0, 0));
//					status.setAlignmentY(.5f);
					status.setBackground(SystemColor.control);
					status.setFont(Font.getFont("sans serif"));
					status.setEditable(false);
					status.setSelectionColor(null);
					status.setSelectedTextColor(null);
					messagePanel.add(status, gbc);
				}
				{ // init and add progress bar
					progressBar = new JProgressBar(0, 10);
					progressBar.setIndeterminate(true);
					gbc.anchor = GridBagConstraints.CENTER;
					gbc.gridy++;
					messagePanel.add(progressBar, gbc);
				}
				{ // init and add cancel button
					cancelButton = new JButton("Cancel");
					cancelButton.addActionListener(new ActionListener()
					{

						@Override
						public void actionPerformed(ActionEvent e)
						{
							step = 20;
							timer.restart();
						}
					});
					gbc.fill = GridBagConstraints.NONE;
					gbc.gridy++;
					messagePanel.add(cancelButton, gbc);
				}
			}
			optionPane = new JOptionPane(
				messagePanel,
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.CANCEL_OPTION,
				null,
				new Object[]{cancelButton},
				cancelButton);
			dialog = optionPane.createDialog("Processing...");
			dialog.setIconImage(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)); // no icon
			dialog.addWindowListener(new ExitOnCloseWindowListener());
		}
		
		timer = new Timer(1000, new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				switch (step++)
				{
					case 4: case 5: case 6: case 7: case 8: case 9: case 10: case 11: case 12: // steps 4 - 12
						progressBar.setValue(step - 4); // go from 0% to 90%
					case 3: // starting step 3, ending step 15
						progressBar.setIndeterminate(false); // set the progress bar to show progress
					case 0: case 1: case 2:   // the first 3 cases before progress is shown,
					case 13: case 14: case 15:// and the last 3 during stalling
						status.setText(message(0, step)); // "Attempting to give a crap.."
						break;
					case 16: case 17: case 18: case 19: // after the stall, before the end
						status.setText(message(1, step)); // "Unable to give a crap. Stopping.."
						break;
					case 20: // the end.
						status.setText(message(2)); // "Process failed, crap not given."
						optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
						cancelButton.setText("Close");
						cancelButton.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								System.exit(0);
							}
						});
						timer.stop();
						break;
				}
			}
		});
		timer.setInitialDelay(0);
		timer.start();
		dialog.setVisible(true);
	}

	private static boolean isVowel(char charAt)
	{
		return
			"AÀÁÂÃÄÅĀĂĄǺȀȂẠẢẤẦẨẪẬẮẰẲẴẶḀÆǼEȄȆḔḖḘḚḜẸẺẼẾỀỂỄỆĒĔĖĘĚÈÉÊËIȈȊḬḮỈỊĨĪĬĮİÌÍÎÏĲOŒØǾȌȎṌṎṐṒỌỎỐỒỔỖỘỚỜỞỠỢŌÒÓŎŐÔÕÖUŨŪŬŮŰŲÙÚÛÜȔȖṲṴṶṸṺỤỦỨỪỬỮỰYẙỲỴỶỸŶŸÝ"
			.indexOf(Character.toUpperCase(charAt)) > 0;
	}

	private static String message(int messageIndex)
	{
		return message(messageIndex, 0);
	}
	private static String message(int messageIndex, int step)
	{
		return
			MESSAGES[messageIndex]
				.replaceAll(new String(new char[]{MSG_REPL_CHAR, MSG_REPL_THING}), thingToGive)                          // "%t" => "crap"
				.replaceAll(new String(new char[]{MSG_REPL_CHAR, MSG_REPL_THING_UPPER}), capitalize(thingToGive))        // "%T" => "Crap"
				.replaceAll(new String(new char[]{MSG_REPL_CHAR, MSG_REPL_THING_SING}), thingSingular)                   // "%s" => "a crap"
				.replaceAll(new String(new char[]{MSG_REPL_CHAR, MSG_REPL_THING_SING_UPPER}), capitalize(thingSingular)) // "%S" => "A crap"
				.replaceAll(new String(new char[]{MSG_REPL_CHAR, MSG_REPL_ELLIP}), ELLIPSIS_PROGRESSION[step % ELLIPSIS_PROGRESSION.length]) // "%e" => ".."
		;
	}

	private static String capitalize(String s)
	{
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	

	private static class ExitOnCloseWindowListener extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			System.exit(0);
		}
	}
}
