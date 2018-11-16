package org.drugis.addis.trialverse.model.trialdata;

import java.net.URI;
import java.util.Objects;

public class Measurement {
  private URI studyUuid;
  private URI variableUri;
  private URI variableConceptUri;
  private URI armUri;
  private URI measurementTypeURI;

  private String survivalTimeScale;
  private Integer sampleSize;
  private Integer rate;
  private Double stdDev;
  private Double stdErr;
  private Double mean;
  private Double exposure;

  private Double confidenceIntervalWidth;
  private Double confidenceIntervalUpperBound;
  private Double confidenceIntervalLowerBound;
  private Double meanDifference;
  private Double standardizedMeanDifference;
  private Double oddsRatio;
  private Double riskRatio;
  private Double hazardRatio;
  private Boolean isLog;
  private URI referenceArm;
  private Double referenceStdErr;

  public Measurement() {
  }

  public Measurement(URI studyUuid, URI variableUri, URI variableConceptUri, URI armUri, URI measurementTypeURI, String survivalTimeScale, Integer sampleSize, Integer rate, Double stdDev, Double stdErr, Double mean, Double exposure, Double confidenceIntervalWidth, Double confidenceIntervalUpperBound, Double confidenceIntervalLowerBound, Double meanDifference, Double standardizedMeanDifference, Double oddsRatio, Double riskRatio, Double hazardRatio, Boolean isLog, URI referenceArm, Double referenceStdErr) {
    this.studyUuid = studyUuid;
    this.variableUri = variableUri;
    this.variableConceptUri = variableConceptUri;
    this.armUri = armUri;
    this.measurementTypeURI = measurementTypeURI;
    this.survivalTimeScale = survivalTimeScale;
    this.sampleSize = sampleSize;
    this.rate = rate;
    this.stdDev = stdDev;
    this.stdErr = stdErr;
    this.mean = mean;
    this.exposure = exposure;
    this.confidenceIntervalWidth = confidenceIntervalWidth;
    this.confidenceIntervalUpperBound = confidenceIntervalUpperBound;
    this.confidenceIntervalLowerBound = confidenceIntervalLowerBound;
    this.meanDifference = meanDifference;
    this.standardizedMeanDifference = standardizedMeanDifference;
    this.oddsRatio = oddsRatio;
    this.riskRatio = riskRatio;
    this.hazardRatio = hazardRatio;
    this.isLog = isLog;
    this.referenceArm = referenceArm;
    this.referenceStdErr = referenceStdErr;
  }

  public URI getStudyUuid() {
    return studyUuid;
  }

  public URI getVariableUri() {
    return variableUri;
  }

  public URI getVariableConceptUri() {
    return variableConceptUri;
  }

  public URI getArmUri() {
    return armUri;
  }

  public URI getMeasurementTypeURI() {
    return measurementTypeURI;
  }

  public String getSurvivalTimeScale() {
    return survivalTimeScale;
  }

  public Integer getSampleSize() {
    return sampleSize;
  }

  public Integer getRate() {
    return rate;
  }

  public Double getStdDev() {
    return stdDev;
  }

  public Double getStdErr() {
    return stdErr;
  }

  public Double getMean() {
    return mean;
  }

  public Double getExposure() {
    return exposure;
  }

  public Double getConfidenceIntervalWidth() {
    return confidenceIntervalWidth;
  }

  public Double getConfidenceIntervalUpperBound() {
    return confidenceIntervalUpperBound;
  }

  public Double getConfidenceIntervalLowerBound() {
    return confidenceIntervalLowerBound;
  }

  public Double getMeanDifference() {
    return meanDifference;
  }

  public Double getStandardizedMeanDifference() {
    return standardizedMeanDifference;
  }

  public Double getOddsRatio() {
    return oddsRatio;
  }

  public Double getRiskRatio() {
    return riskRatio;
  }

  public Double getHazardRatio() {
    return hazardRatio;
  }

  public Boolean getLog() {
    return isLog;
  }

  public URI getReferenceArm() {
    return referenceArm;
  }

  public Double getReferenceStdErr() {
    return referenceStdErr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Measurement that = (Measurement) o;
    return Objects.equals(studyUuid, that.studyUuid) &&
            Objects.equals(variableUri, that.variableUri) &&
            Objects.equals(variableConceptUri, that.variableConceptUri) &&
            Objects.equals(armUri, that.armUri) &&
            Objects.equals(measurementTypeURI, that.measurementTypeURI) &&
            Objects.equals(survivalTimeScale, that.survivalTimeScale) &&
            Objects.equals(sampleSize, that.sampleSize) &&
            Objects.equals(rate, that.rate) &&
            Objects.equals(stdDev, that.stdDev) &&
            Objects.equals(stdErr, that.stdErr) &&
            Objects.equals(mean, that.mean) &&
            Objects.equals(exposure, that.exposure) &&
            Objects.equals(confidenceIntervalWidth, that.confidenceIntervalWidth) &&
            Objects.equals(confidenceIntervalUpperBound, that.confidenceIntervalUpperBound) &&
            Objects.equals(confidenceIntervalLowerBound, that.confidenceIntervalLowerBound) &&
            Objects.equals(meanDifference, that.meanDifference) &&
            Objects.equals(standardizedMeanDifference, that.standardizedMeanDifference) &&
            Objects.equals(oddsRatio, that.oddsRatio) &&
            Objects.equals(riskRatio, that.riskRatio) &&
            Objects.equals(hazardRatio, that.hazardRatio) &&
            Objects.equals(isLog, that.isLog) &&
            Objects.equals(referenceArm, that.referenceArm) &&
            Objects.equals(referenceStdErr, that.referenceStdErr);
  }

  @Override
  public int hashCode() {

    return Objects.hash(studyUuid, variableUri, variableConceptUri, armUri, measurementTypeURI, survivalTimeScale, sampleSize, rate, stdDev, stdErr, mean, exposure, confidenceIntervalWidth, confidenceIntervalUpperBound, confidenceIntervalLowerBound, meanDifference, standardizedMeanDifference, oddsRatio, riskRatio, hazardRatio, isLog, referenceArm, referenceStdErr);
  }
}
