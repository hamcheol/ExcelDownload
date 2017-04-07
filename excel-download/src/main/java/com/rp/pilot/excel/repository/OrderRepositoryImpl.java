package com.rp.pilot.excel.repository;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rp.pilot.excel.model.Order;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
	private Logger logger = LoggerFactory.getLogger(getClass());

	// private static final String BASE_DIR = "C:\\Temp\\";
	private static final String BASE_DIR = "/home/hamcheol/temp";

	private final String NAMESPACE = "com.rp.pilot.excel.repository.OrderRepositoryImpl.";

	@Autowired
	private SqlSession sqlSession;

	@Override
	public void insertOrder(Order order) {
		sqlSession.insert(NAMESPACE + "insertOrder", order);
	}

	@Override
	public int selectTotalOrderCount() {
		return sqlSession.selectOne(NAMESPACE + "selectTotalOrderCount");
	}

	@Override
	public void csvDownload(int pageSize) {
		try {
			final int lastPage = (selectTotalOrderCount() / pageSize) + 1;
			Path baseDir = Paths.get(BASE_DIR);
			String uniqueId = Integer.toString(RandomUtils.nextInt(0, 1000));
			Path tmpDir = Files.createTempDirectory(baseDir, uniqueId);

			logger.info("tmpDir : " + tmpDir);

			sqlSession.select(NAMESPACE + "selectTotalOrders", null, new ResultHandler<Order>() {
				List<String> lines = new ArrayList<>();
				int idx = 0;
				int pageNo = 1;

				@Override
				public void handleResult(ResultContext<? extends Order> context) {
					int count = context.getResultCount();
					Order order = context.getResultObject();
					lines.add(order.toString());
					idx++;
					logger.info("idx: {} count:{}", idx, count);

					if (idx == pageSize) {
						pageNo = (count / pageSize) + 1;
						tempFileWrite(lines, pageNo);
						idx = 0;
						lines.clear();
					}
				}

				public void tempFileWrite(List<String> lines, int pageNo) {
					try {
						String prefix = "excel_";
						String suffix = ".tmp";
						Path tmpfile = Files.createTempFile(tmpDir, prefix, suffix);
						// FileUtils.writeLines(tmpfile., lines);
						Files.write(tmpfile, lines);
						// tmpfile.toFile().deleteOnExit();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			tempFileMerge(tmpDir, uniqueId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void tempFileMerge(Path tmpDir, String uniqueId) throws IOException {
		DirectoryStream<Path> ds = Files.newDirectoryStream(tmpDir);

		FileChannel mergeFile = FileChannel.open(Paths.get(tmpDir + uniqueId + ".csv"), StandardOpenOption.CREATE,
				StandardOpenOption.WRITE);

		for (Path path : ds) {
			logger.info(path.toString());
			FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);

			for (long p = 0, l = channel.size(); p < l;) {
				p += channel.transferTo(p, l - p, mergeFile);
			}
		}
	}
}
