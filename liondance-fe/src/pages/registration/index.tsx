import React, { useState } from "react";
import { useForm } from "@mantine/form";
import { Autocomplete, Loader } from "@mantine/core";

function Registration() {
  const [emailData, setEmailData] = useState<string[]>([]);
  const [emailDataLoading, setEmailDataLoading] = useState<boolean>(false);
  const form = useForm({
    mode: "controlled",
    initialValues: {
      email: "",
    },

    validate: {
      email: (value) => (/^\S+@\S+$/.test(value) ? null : "Invalid email"),
    },
  });

  form.watch("email", ({ value }) => {
    setEmailDataLoading(true);

    if (value.trim().length === 0 || value.includes("@")) {
      setEmailDataLoading(false);
    } else {
      setEmailDataLoading(true);
      setEmailData(
        ["gmail.com", "outlook.com", "hotmail.com", "yahoo.com"].map(
          (prov) => `${value}@${prov}`
        )
      );
      setEmailDataLoading(false);
    }
  });

  return (
    <div>
      <form>
        <Autocomplete
          rightSection={emailDataLoading ? <Loader size={12} /> : null}
          placeholder="E-Mail Address"
          data={emailData}
          {...form.getInputProps("email")}
        />
      </form>
    </div>
  );
}

export default Registration;
