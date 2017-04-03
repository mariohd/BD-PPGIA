package userInterface.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import db.modules.buffer.BufferController;
import db.modules.buffer.algorithm.BufferAlgorithm;
import db.modules.metaStructure.PageBlock;

public class BufferFrame extends JFrame {
	JProgressBar progressBar;
	BufferAlgorithm<PageBlock> buffer =  BufferController.getLRUAlgorithm();
	private JPanel bufferPagesPanel;
	private JLabel hitLabel, missLabel, HRLabel, pagesSizeLabel;

	public BufferFrame() {
		this.progressBar = new JProgressBar(0, buffer.getSize());
		this.bufferPagesPanel = new JPanel(new GridLayout(0, 10));
		
		this.hitLabel = new JLabel("", JLabel.CENTER);
		this.missLabel = new JLabel("", JLabel.CENTER);
		this.HRLabel = new JLabel("", JLabel.CENTER);
		this.pagesSizeLabel = new JLabel("", JLabel.CENTER);
		
		init();
		config();
		mount();
		mountPages();
		startUpdateThread();
		setVisible(true);
	}
	
	private void config() {
		progressBar.setValue(buffer.getCollection().size());
		progressBar.setStringPainted(true);	
	}
	
	private void changeLabels() {
		hitLabel.setText("Hits: " + buffer.getHits() + "\t");
		missLabel.setText("Miss: " + buffer.getMisses() + "\t");
		HRLabel.setText(String.format("Hit Rate: %.3f", buffer.getHitRatio()));
		pagesSizeLabel.setText(buffer.getCollection().size() + "/" + buffer.getSize());
	}

	private void mount() {
		JPanel northPanel = new JPanel(new FlowLayout());
		northPanel.add(hitLabel);
		northPanel.add(missLabel);
		northPanel.add(HRLabel);

		add(northPanel, BorderLayout.NORTH);
		
		add(new JScrollPane(bufferPagesPanel), BorderLayout.CENTER);
		JPanel bufferProgressPanel = new JPanel(new BorderLayout());
		bufferProgressPanel.add(pagesSizeLabel, BorderLayout.NORTH);
		bufferProgressPanel.add(progressBar, BorderLayout.CENTER);
		
		add(bufferProgressPanel, BorderLayout.SOUTH);
	}

	private void init() {
		this.setLayout(new BorderLayout());
		Dimension resolution = Utils.screenResolution();
		resolution.setSize(resolution.getWidth() * 0.5, resolution.getHeight() * 0.5);
		this.setSize(resolution);
		this.setLocationRelativeTo(null);
		this.setTitle("Buffer");
		this.setResizable(false);
		this.setLayout(new BorderLayout());
	}
	
	private void mountPages() {
		List<PageBlock> collection = buffer.getCollection();
		bufferPagesPanel.setBackground(Color.white);
		bufferPagesPanel.removeAll();
		for (Iterator<PageBlock> iterator = collection.iterator(); iterator.hasNext();) {
			PageBlock pageBlock = iterator.next();
			JLabel jLabel = new JLabel(pageBlock.toString());
			jLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			bufferPagesPanel.add(jLabel);
		}
	}
	
	private void startUpdateThread() {
		Thread update = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						progressBar.setValue(buffer.getCollection().size());
						changeLabels();
						mountPages();
						revalidate();
						repaint();
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}
			}
		});
		update.start();
	}
}
