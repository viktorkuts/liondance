import React from "react";
import "./StudentDetailsOverlay.css";
import { Button } from "@mantine/core";
import userService from "@/services/userService";
import { RegistrationStatus } from "@/models/Users";

interface StudentDetailsOverlayProps {
  student: {
    userId: string;
    firstName: string;
    middleName?: string;
    lastName: string;
    gender: string;
    dob: string;
    email: string;
    phone?: string;
    joinDate: string;
    registrationStatus: string;
    address?: {
      streetAddress: string;
      city: string;
      state: string;
      zip: string;
    };
  };
  onClose: (refresh?: boolean) => void;
}

const formatDate = (isoDate: string): string => {
  const date = new Date(isoDate);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

const StudentDetailsOverlay: React.FC<StudentDetailsOverlayProps> = ({
  student,
  onClose,
}) => {
  const submitRegistrationStatus = async (isApproved: boolean) => {
    userService.updateRegistrationStatus(student.userId, {
      registrationStatus: isApproved
        ? RegistrationStatus.ACTIVE
        : RegistrationStatus.INACTIVE,
    });
    onClose(true);
  };

  return (
    <div className="overlay">
      <div className="overlay-content">
        <h2 className="overlay-title">Student Details</h2>

        <div className="overlay-details">
          {/* Row 1: First Name, Middle Name, Last Name */}
          <div className="detail-row">
            <div className="detail-item">
              <label>First Name</label>
              <div>{student.firstName}</div>
            </div>
            <div className="detail-item">
              <label>Middle Name</label>
              <div>{student.middleName ?? "N/A"}</div>
            </div>
            <div className="detail-item">
              <label>Last Name</label>
              <div>{student.lastName}</div>
            </div>
          </div>

          {/* Row 2: Date of Birth and Phone Number */}
          <div className="detail-row">
            <div className="detail-item">
              <label>Date of Birth</label>
              <div>{student.dob}</div>
            </div>
            <div className="detail-item">
              <label>Phone Number</label>
              <div>{student.phone ?? "N/A"}</div>
            </div>
          </div>

          {/* Row 3: Email Address */}
          <div className="detail-item">
            <label>Email Address</label>
            <div>{student.email}</div>
          </div>

          {/* Row 4: Address */}
          <div className="detail-item">
            <label>Address</label>
            <div>{student.address?.streetAddress ?? "N/A"}</div>
          </div>

          {/* Row 5: City, Province, ZIP */}
          <div className="detail-row">
            <div className="detail-item">
              <label>City</label>
              <div>{student.address?.city ?? "N/A"}</div>
            </div>
            <div className="detail-item">
              <label>Province</label>
              <div>{student.address?.state ?? "N/A"}</div>
            </div>
            <div className="detail-item">
              <label>ZIP Code</label>
              <div>{student.address?.zip ?? "N/A"}</div>
            </div>
          </div>

          {/* Row 6: Join Date and Registration Status */}
          <div className="detail-row">
            <div className="detail-item">
              <label>Join Date</label>
              <div>{formatDate(student.joinDate)}</div>
            </div>
            <div className="detail-item">
              <label>Registration Status</label>
              <div>{student.registrationStatus}</div>
            </div>
          </div>
        </div>

        {/* Bottom Right Close Button */}
        <div className="close-button-container">
          <Button
            variant="default"
            onClick={() => submitRegistrationStatus(false)}
          >
            Deny
          </Button>
          <Button onClick={() => submitRegistrationStatus(true)}>
            Approve
          </Button>
          <button className="close-button" onClick={() => onClose(false)}>
            Close
          </button>
        </div>
      </div>
    </div>
  );
};

export default StudentDetailsOverlay;
