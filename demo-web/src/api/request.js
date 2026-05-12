import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.response.use(
  res => {
    const data = res.data
    if (data.code === 200) {
      return data.data
    }
    ElMessage.error(data.message || '请求失败')
    return Promise.reject(new Error(data.message))
  },
  err => {
    ElMessage.error('网络错误: ' + err.message)
    return Promise.reject(err)
  }
)

export default request
