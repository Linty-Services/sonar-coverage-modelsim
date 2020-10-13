/*
 * SonarQube Linty ModelSim :: Plugin
 * Copyright (C) 2019-2020 Linty Services
 * mailto:contact@linty-services.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.lintyservices.sonar.plugins.modelsim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.Settings;
import org.sonar.api.scan.filesystem.PathResolver;

import java.io.File;

public class ModelsimSensor implements Sensor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ModelsimSensor.class);

  private FileSystem fs;
  private PathResolver pathResolver;
  private final Configuration configuration;

  public ModelsimSensor(FileSystem fs, PathResolver pathResolver, Settings settings,
                        Configuration configuration) {
    this.fs = fs;
    this.pathResolver = pathResolver;
    this.configuration = configuration;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("ModelsimSensor");
  }

  @Override
  public void execute(SensorContext context) {
    String path = configuration.get(ModelsimPlugin.MODELSIM_REPORT_PATH_PROPERTY).orElse(null);
    String mode = configuration.get(ModelsimPlugin.MODELSIM_REPORT_MODE).orElse(null);
    File report = pathResolver.relativeFile(fs.baseDir(), path);
    if (!report.isFile() || !report.exists() || !report.canRead()) {
      LOGGER.warn("Modelsim report not found at {}", report);
    } else {
      parseReport(report, context, mode);
    }
  }

  protected void parseReport(File xmlFile, SensorContext context, String mode) {
    LOGGER.info("parsing {}", xmlFile);
    ModelsimReportParser.parseReport(xmlFile, context, mode);
  }

}