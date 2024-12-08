import axios from "axios";
import { City, Province } from "@/types/geo";

let provincesCache = [
  {
    term: "NL",
    name: "Newfoundland and Labrador",
    code: "-1",
  },
  {
    name: "Prince Edward Island",
    term: "PE",
    code: "-1",
  },
  {
    name: "Nova Scotia",
    term: "NS",
    code: "-1",
  },
  {
    name: "New Brunswick",
    term: "NB",
    code: "-1",
  },
  {
    name: "Quebec",
    term: "QC",
    code: "-1",
  },
  {
    name: "Ontario",
    term: "ON",
    code: "-1",
  },
  {
    name: "Manitoba",
    term: "MB",
    code: "-1",
  },
  {
    name: "Saskatchewan",
    term: "SK",
    code: "-1",
  },
  {
    name: "Alberta",
    term: "AB",
    code: "-1",
  },
  {
    name: "British Columbia",
    term: "BC",
    code: "-1",
  },
  {
    name: "Yukon",
    term: "YT",
    code: "-1",
  },
  {
    name: "Northwest Territories",
    term: "NT",
    code: "-1",
  },
  {
    name: "Nunavut",
    term: "NU",
    code: "-1",
  },
];

interface Definition {
  code: string;
  term: string;
  description: string;
}

interface Item {
  name: string;
}

interface ProvinceResponseModel {
  definitions: Definition[];
}

interface CityResponseModel {
  items: Item[];
}

const provinces = async () => {
  if (provincesCache[0]?.code === "-1") {
    provincesCache = [];
    const res = await axios.get<ProvinceResponseModel>(
      "https://geogratis.gc.ca/services/geoname/en/codes/province.json"
    );

    if (res.status == 200) {
      if (res.data.definitions) {
        res.data.definitions.map((val) => {
          const newProvince: Province = {
            name: val.description,
            term: val.term,
            code: val.code,
          };
          provincesCache.push(newProvince);
        });
      }
    }
  }

  return provincesCache;
};

const getCities = async (cityName: string, provinceCode: string) => {
  const res = await axios.get<CityResponseModel>(
    `https://geogratis.gc.ca/services/geoname/en/geonames.json?q=${cityName}*&category=O&theme=985&province=${provinceCode}&sort-field=concise&concise=CITY&concise=TOWN&concise=VILG&concise=HAM`
  );
  let cities: City[] = [];

  if (res.status == 200) {
    if (res.data.items) {
      res.data.items.map((val) => {
        const city: City = {
          name: val.name,
        };
        cities.push(city);
      });
      cities = [...new Set(cities.map((v) => v.name))].map((v) => {
        return { name: v };
      });
    }
  }

  return cities;
};

export default {
  getCities,
  provinces,
  provincesCache,
};
