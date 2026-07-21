import { InfluxDB, Point } from '@influxdata/influxdb-client'

const influxConfig = {
  url: process.env.VUE_APP_INFLUX_URL || 'http://localhost:8086',
  token: process.env.VUE_APP_INFLUX_TOKEN || '',
  org: process.env.VUE_APP_INFLUX_ORG || '',
  bucket: process.env.VUE_APP_INFLUX_BUCKET || ''
}

const influxClient = new InfluxDB({
  url: influxConfig.url,
  token: influxConfig.token
})

const writeApi = influxClient.getWriteApi(influxConfig.org, influxConfig.bucket)
const queryApi = influxClient.getQueryApi(influxConfig.org)

export default {
  writePoint(measurement, fields, tags = {}, timestamp = new Date()) {
    const point = new Point(measurement)
    for (const [key, value] of Object.entries(tags)) {
      point.tag(key, value)
    }
    for (const [key, value] of Object.entries(fields)) {
      point.floatField(key, value)
    }
    point.timestamp(timestamp)
    writeApi.writePoint(point)
  },

  async query(fluxQuery) {
    return new Promise((resolve, reject) => {
      const results = []
      queryApi.queryRows(fluxQuery, {
        next(row, tableMeta) {
          results.push(tableMeta.toObject(row))
        },
        error(error) {
          reject(error)
        },
        complete() {
          resolve(results)
        }
      })
    })
  },

  async flush() {
    return writeApi.flush()
  },

  close() {
    writeApi.close()
  }
}