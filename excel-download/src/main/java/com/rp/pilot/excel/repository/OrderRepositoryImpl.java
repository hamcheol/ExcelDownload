package com.rp.pilot.excel.repository;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

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

	private static final String BASE_DIR = "C:\\Temp\\";

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
	public String csvDownload(int pageSize) {
		try {
			Path baseDir = Paths.get(BASE_DIR);
			String uniqueId = Integer.toString(RandomUtils.nextInt(0, 1000));
			Path tmpDir = Files.createTempDirectory(baseDir, uniqueId);

			logger.info("tmpDir : " + tmpDir); // 임시 파일 디렉토리

			sqlSession.select(NAMESPACE + "selectTotalOrders", null, new ResultHandler<Order>() {
				List<String> lines = new ArrayList<>(); // 조회데이터 ROWS
				int idx = 0; // 임시파일의 ROW Index
				int pageNo = 1; // 임시파일 Index

				@Override
				public void handleResult(ResultContext<? extends Order> context) {
					int count = context.getResultCount();
					Order order = context.getResultObject();
					lines.add(order.toString());
					idx++;
					logger.info("idx: {} count:{}", idx, count);

					// 파일 하나의 ROW사이즈만큼 되면
					if (idx == pageSize) {
						pageNo = (count / pageSize) + 1;
						// 임시파일에 쓰고
						tempFileWrite(lines, pageNo);
						idx = 0;
						// List는 clear한다.
						lines.clear();
					}
				}

				/**
				 * 임시 파일 쓰기
				 * 
				 * @param lines
				 * @param pageNo
				 */
				public void tempFileWrite(List<String> lines, int pageNo) {
					try {
						String prefix = "excel_";
						String suffix = ".tmp";
						Path tmpfile = Files.createTempFile(tmpDir, prefix, suffix);
						Files.write(tmpfile, lines);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});

			return tempFileMerge(tmpDir, uniqueId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 조각조각 기록된 임시파일을 파일 하나로 merge한다.
	 * 
	 * @param tmpDir
	 * @param uniqueId
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private String tempFileMerge(Path tmpDir, String uniqueId) {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(tmpDir, "*.tmp")) {			
			FileChannel mergeFile = FileChannel.open(Paths.get(tmpDir + uniqueId + ".csv"), StandardOpenOption.CREATE,
					StandardOpenOption.WRITE);

			for (Path path : ds) {
				logger.info(path.toString());
				FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);

				for (long p = 0, l = channel.size(); p < l;) {
					logger.info("p:" + p + " l:" + l);
					p += channel.transferTo(p, l - p, mergeFile);
				}
			}

			return mergeFile.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
