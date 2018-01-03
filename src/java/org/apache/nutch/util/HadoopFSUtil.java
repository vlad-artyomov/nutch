/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.util;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

public class HadoopFSUtil {

  /**
   * Returns PathFilter that passes all paths through.
   */
  public static PathFilter getPassAllFilter() {
    return arg0 -> true;
  }

  /**
   * Returns PathFilter that passes directories through.
   */
  public static PathFilter getPassDirectoriesFilter(final FileSystem fs) {
    return path -> {
      try {
        return fs.getFileStatus(path).isDirectory();
      } catch (IOException ioe) {
        return false;
      }
    };
  }

  /**
   * Turns an array of FileStatus into an array of Paths.
   */
  public static Path[] getPaths(FileStatus[] stats) {
    if (stats == null) {
      return null;
    }
    if (stats.length == 0) {
      return new Path[0];
    }
    Path[] res = new Path[stats.length];
    for (int i = 0; i < stats.length; i++) {
      res[i] = stats[i].getPath();
    }
    return res;
  }

  /**
   * Returns path of last modified segment in segments directory on HDFS.
   *
   * @param segmentDir folder where all segments located
   * @param configuration Hadoop configuration
   * @return Path of newest segment.
   * @throws IOException in case of IO errors.
   */
  public static Path getLastModifiedSegment(String segmentDir, Configuration configuration) throws IOException {
    Path segment_dir = new Path(segmentDir);
    FileSystem fs = segment_dir.getFileSystem(configuration);
    FileStatus[] fileStatuses = fs.listStatus(segment_dir);
    Arrays.sort(fileStatuses, (f1, f2) -> {
      if (f1.getModificationTime() > f2.getModificationTime())
        return -1;
      else
        return 0;
    });
    return fileStatuses[0].getPath();
  }

}
