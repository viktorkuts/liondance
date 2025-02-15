import React, { useEffect, useState } from "react";
import "./ClassFeedbackReportPage.css";
import { Notification, Loader } from "@mantine/core";
import { useTranslation } from "react-i18next";
import useClassFeedbackService from "@/services/classFeedbackService";
import { ClassFeedbackReportResponseModel } from "@/models/ClassFeedback";

const ClassFeedbackReportPage: React.FC = () => {
  const { getAllReports, downloadReport } = useClassFeedbackService();
  const [reports, setReports] = useState<ClassFeedbackReportResponseModel[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const { t } = useTranslation();

  useEffect(() => {
    setLoading(true);
    getAllReports()
      .then((data) => {
        setReports(data);
        setLoading(false);
      })
      .catch((err) => {
        setError("Failed to fetch reports: " + err.message);
        setLoading(false);
      });
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleDownload = async (reportId: string) => {
    try {
      const blob = await downloadReport(reportId);
      const url = window.URL.createObjectURL(
        new Blob([blob], { type: "application/pdf" })
      );
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `report-${reportId}.pdf`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      setError("Failed to download report: " + err.message);
    }
  };

  return (
    <div className="report-page-container">
      <h1>{t("Class Feedback Reports")}</h1>

      {loading && <Loader />}
      {error && <Notification color="red">{error}</Notification>}
      {!loading && reports.length === 0 && <p>{t("No reports available.")}</p>}

      {!loading && reports.length > 0 && (
        <table className="report-table">
          <thead>
            <tr>
              <th>{t("Date")}</th>
              <th>{t("Average Score")}</th>
              <th>{t("Actions")}</th>
            </tr>
          </thead>
          <tbody>
            {reports.map((report) => (
              <tr key={report.reportId}>
                <td>{report.classDate}</td>
                <td>{report.averageScore.toFixed(2)}</td>
                <td>
                  <button
                    className="download-button"
                    onClick={() => handleDownload(report.reportId)}
                  >
                    {t("Download PDF")}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default ClassFeedbackReportPage;


