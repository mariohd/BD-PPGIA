package db.modules.metaStructure;

import java.io.IOException;
import java.io.RandomAccessFile;

import db.Utils;
import db.modules.dataStructure.Table;
import db.modules.fs.FileSystem;

public class DataBlockHeader {
	private Table parent;
	private byte container;
	private byte[] blockId = new byte[3];
	private byte blockType = 1;
	private byte[] usedSpace = new byte[3];
	public final int blockBeginning;
	
	public DataBlockHeader(Table parent, int blockId) {
		this.parent = parent;
		this.container = this.parent.getContainer();
		this.blockId = Utils.toByteArray(blockId, 3);
		this.blockBeginning = FileSystem.pageSize * blockId;
	}

	public int getUsedSpace() {
		return Utils.toInt(usedSpace, 0);
	}

	public boolean updateUsedSpace(int newSize) throws IOException {
		usedSpace = Utils.toByteArray(newSize, 3);

		RandomAccessFile file = parent.getContainerFile();

		file.seek(blockBeginning + 5);
		file.write(usedSpace);

		return true;
	}

	public boolean save() throws IOException {
		RandomAccessFile file = parent.getContainerFile();

		file.seek(blockBeginning);
		file.write(container);

		file.seek(blockBeginning + 1);
		file.write(blockId);

		file.seek(blockBeginning + 4);
		file.write(blockType);

		file.seek(blockBeginning + 5);
		file.write(usedSpace);

		return true;
	}

	public boolean load() throws IOException {
		RandomAccessFile file = parent.getContainerFile();

		try {
			file.seek(blockBeginning);
			this.container = file.readByte();

			file.seek(blockBeginning + 1);
			file.read(this.blockId);

			file.seek(blockBeginning + 4);
			this.blockType = file.readByte();

			file.seek(blockBeginning + 5);
			file.read(this.usedSpace);
		} catch (IOException e) {
			this.save();
			this.load();
		}

		return true;
	}
	
	public int writeStart() {
		return this.blockBeginning + getUsedSpace() + 8;
	}

	public int readStart() {
		return this.blockBeginning + 8;
	}
	
	public String generateNewRowid() {
		return this.container + "." + Utils.toInt(this.blockId, 0) + "." + this.writeStart();
	}

	public void print() {
		System.out.println("\tCONTAINER: " + this.container);
		System.out.println("\tID DO BLOCO: " + Utils.toInt(blockId, 0));
		System.out.println("\tTIPO DO BLOCK: " + this.blockType);
		System.out.println("\tESPAÇO USADO: " + Utils.toInt(this.usedSpace, 0));
	}
}
